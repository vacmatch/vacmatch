package com.vac.manager.model.staff

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.team.Team
import java.util.Calendar
import com.vac.manager.model.personal.Address
import scala.collection.JavaConverters._
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation
import java.text.SimpleDateFormat
import java.util.ArrayList
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PERSON")
@Inheritance(strategy = InheritanceType.JOINED)
class Person(
    stName: String,
    stSurname: String,
    stEmail: String,
    stTelephones: String,
    stCardId: String,
    stBirthdate: Calendar,
    stFederation: Federation
) {

  @Id
  @SequenceGenerator(name = "personIdGenerator", sequenceName = "person_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "personIdGenerator")
  var personId: java.lang.Long = _

  @BeanProperty
  @Column(nullable = false)
  var name: String = stName

  @BeanProperty
  @Column(nullable = false)
  var surname: String = stSurname

  @BeanProperty
  @Column
  var alias: String = null

  @BeanProperty
  @Column
  var email: String = stEmail

  @BeanProperty
  @Column
  var avatarLink: String = Gravatar(stEmail).ssl(true).avatarUrl

  @BeanProperty
  @Column
  var telephones: String = stTelephones

  @BeanProperty
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "addressId")
  var address: Address = _

  @BeanProperty
  @Column
  var cardId: String = stCardId

  @BeanProperty
  @Column
  var birthdate: Calendar = stBirthdate

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "fedId")
  var federation: Federation = stFederation

  def this() = this("", "", "", null, "", Calendar.getInstance(), null)

  override def equals(obj: Any): Boolean = {
    if ((obj == null) || (!obj.isInstanceOf[Person]))
      return false
    var personObj: Person = obj.asInstanceOf[Person]
    (personObj.personId == this.personId) &&
      (personObj.name == this.name) &&
      (personObj.surname == this.surname) &&
      (personObj.alias == this.alias) &&
      (personObj.email == this.email) &&
      (personObj.telephones == this.telephones) &&
      (personObj.address == this.address) &&
      (personObj.cardId == this.cardId) &&
      (personObj.birthdate == this.birthdate) &&
      (personObj.federation == this.federation)
  }

  override def toString = "(" + this.personId + ") " +
    this.surname +
    ", " + this.name +
    "\nCardId: " + this.cardId +
    "\nEmail: " + this.email +
    "\nAlias: " + this.alias +
    "\nTelephones: " + this.telephones +
    "\nAddress: " + this.address +
    "\nBirthdate: " + (new SimpleDateFormat("yyyy MMM dd HH:mm:ss"))
    .format(this.birthdate.getTime()) +
    "\nFederation: " + this.federation

}
