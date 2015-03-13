package com.vac.manager.service.staff

import com.vac.manager.model.staff.Person
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait PersonService {

  def find(personId: Long): Option[Person]

  def findAllByFederationId(fedId: Long): Seq[Person]

  def findByName(name: String, startIndex: Int, count: Int): Seq[Person]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Person]

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Person]

  @throws[InstanceNotFoundException]
  def changePrivacy(personId: Long, newState: Boolean, newAlias: String): Person

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createPerson(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stCardId: String,
    stBirth: Calendar, idFederation: Long): Person

  @throws[IllegalArgumentException]
  def modifyPerson(personId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stCardId: String, stBirth: Calendar): Option[Person]

  def assignAddress(personId: Long, stAddress: Address): Option[Person]

  protected def checkParameters(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stCardId: String)

}
