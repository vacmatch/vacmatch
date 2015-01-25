package com.vac.manager.auth.model

import com.vac.manager.model.federation.Federation
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Transient
import javax.persistence.{ Column, ElementCollection, Entity, Id }
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Entity
class User extends UserDetails {

  @Id
  @BeanProperty
  var id: java.lang.Long = _

  @Column
  @BeanProperty
  var username: String = _

  @Column
  @BeanProperty
  var encPasswd: String = _

  @Column
  @BeanProperty
  var fullName: String = _

  @BeanProperty
  @ManyToOne(fetch=FetchType.EAGER, optional = false)
  var federation: Federation = _

  @Column
  @BeanProperty
  @ElementCollection
  var roles: java.util.List[String] = _

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
    return roles.asScala.map { new SimpleGrantedAuthority(_) }.asJava
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

    println("Credentials expire at " + expiresAt + " and it is currently " + now + " before = " + (expiresAt before now) + " after = " + (expiresAt after now) + "compareTo = " + (expiresAt compareTo now))

    return true
    return expiresAt before now
  }

  @Transient
  def isEnabled(): Boolean = enabled

}
