package com.vac.manager.auth.model

import com.vac.manager.model.federation.daojpa._
import com.vac.manager.model.federation.Federation
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Transient
import javax.persistence.{ Column, ElementCollection, Entity, Id }
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Entity
@Table(name = "userdetails")
class User extends UserDetails {
  @Id
  @SequenceGenerator(name = "userIdGenerator", sequenceName = "user_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "userIdGenerator")
  @BeanProperty
  var id: java.lang.Long = _

  @BeanProperty
  var username: String = _

  @Column
  @BeanProperty
  var encPasswd: String = _

  @Column
  @BeanProperty
  var fullName: String = _

  @Column
  @BeanProperty
  var email: String = _

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @BeanProperty
  var federation: Federations = _

  @BeanProperty
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable
  var roles: java.util.Set[UserRole] = _

  @Column
  @BeanProperty
  var locked: Boolean = _

  @Column
  @BeanProperty
  var enabled: Boolean = _

  @Column
  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP)
  var lastPasswordChange: Calendar = _

  @Transient
  def getAuthorities(): java.util.Collection[_ <: org.springframework.security.core.GrantedAuthority] = {
    return roles.asScala.map { r => new SimpleGrantedAuthority("ROLE_" + r.name) }.asJava
  }

  @Transient
  def getPassword(): String = encPasswd

  @Transient
  def isAccountNonExpired(): Boolean = true

  @Transient
  def isAccountNonLocked(): Boolean = !locked

  @Transient
  def isCredentialsNonExpired(): Boolean = {

    val now = new Date
    val saved = lastPasswordChange.getTime.clone.asInstanceOf[Date]

    val expiresAtCal = new GregorianCalendar
    expiresAtCal.setTime(saved)
    expiresAtCal.add(Calendar.DATE, 1)

    val expiresAt = expiresAtCal.getTime

    return true
    return expiresAt before now
  }

  @Transient
  def isEnabled(): Boolean = enabled

}
