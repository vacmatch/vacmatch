package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.PersonDao
import com.vac.manager.model.staff.Person
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import com.vac.manager.model.personal.Address
import java.util.Calendar
import org.springframework.transaction.annotation.Transactional
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.personal.AddressService

@Service("personService")
@Transactional
class PersonServiceImpl extends PersonService {

  @Autowired
  var federationService: FederationService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personDao: PersonDao = _

  /* --------------- FIND ---------------- */

  def find(personId: Long): Option[Person] = {
    personDao.findById(personId)
  }

  def findAllByFederationId(fedId: Long): Seq[Person] = {
    personDao.findAllByFederationId(fedId)
  }

  def findByName(name: String, startIndex: Int, count: Int): Seq[Person] = {
    personDao.findByName(name, startIndex, count)
  }

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Person] = {
    personDao.findAllByActivated(activated, startIndex, count)
  }

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Person] = {
    personDao.findByEmail(email, startIndex, count)
  }

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Person] = {
    personDao.findByCardId(cardId, startIndex, count)
  }

  /* ---------------- MODIFY --------------- */

  def changeActivation(personId: Long, newState: Boolean) = {
    var maybePerson: Option[Person] = personDao.findById(personId)

    maybePerson match {
      case None => throw new InstanceNotFoundException(personId, classOf[Person].getName())
      case Some(person) => {
        person.activated = newState
        personDao.save(person)
      }
    }
  }

  def changePrivacy(personId: Long, newState: Boolean, newAlias: String) = {
    var person: Option[Person] = personDao.findById(personId)

    person.map(_.alias = newAlias)
    person.map(personDao.save(_))
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createPerson(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String,
    stCardId: String, stBirth: Calendar, idFederation: Long): Person = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stCardId)

    var maybeFederation: Option[Federation] = federationService.find(idFederation)

    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
        var person: Person = new Person(stName, stSurnames, stEmail, stTelephones,
          stCardId, stBirth, stFederation)

        personDao.save(person)
        person
      }
    }
  }

  @throws[IllegalArgumentException]
  def modifyPerson(personId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stCardId: String, stBirth: Calendar): Option[Person] = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stCardId)

    //Modify address
    var maybePerson: Option[Person] = assignAddress(personId, stAddress)

    maybePerson.map {
      person =>
        {
          person.name = stName
          person.surnames = stSurnames
          person.email = stEmail
          person.avatarLink = new Gravatar(if (Option(stEmail).isEmpty) "" else stEmail).ssl(true).avatarUrl
          person.telephones = stTelephones
          person.cardId = stCardId
          person.birth = stBirth
          personDao.save(person)
        }
    }
    maybePerson
  }

  def assignAddress(personId: Long, stAddress: Address): Option[Person] = {

    val maybePerson: Option[Person] = find(personId)

    maybePerson.map {
      person =>
        {
          if ((Option(stAddress).isEmpty) || (person.address == stAddress))
            return maybePerson

          if (!Option(person.address).isEmpty)
            addressService.removeAddress(person.address.addressId)

          val savedAddress: Address = addressService.createAddress(
            stAddress.firstLine, stAddress.secondLine,
            stAddress.postCode, stAddress.locality,
            stAddress.province, stAddress.country)

          person.address = savedAddress
          personDao.save(person)
        }
    }
    maybePerson
  }

  @throws[IllegalArgumentException]
  protected def checkParameters(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stCardId: String) {

    val checkAgainstNull = List((stName, classOf[String]), (stSurnames, classOf[String]),
      (stCardId, classOf[String]), (stBirth, classOf[Calendar]))
    val checkAgainstEmpty = List((stName, classOf[String]), (stSurnames,
      classOf[String]), (stCardId, classOf[String]))

    checkAgainstNull.map {
      case (elt, cls) =>
        if (Option(elt).isEmpty)
          throw new IllegalArgumentException(elt, cls.getName())
    }
    checkAgainstEmpty.map {
      case (elt, cls) =>
        if (Option(elt).exists(_.trim == ""))
          throw new IllegalArgumentException(elt, cls.getName())
    }
  }

}

