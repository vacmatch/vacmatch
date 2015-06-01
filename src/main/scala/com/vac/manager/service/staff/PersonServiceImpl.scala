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
import com.vac.manager.model.staff.StaffMemberDao

@Service("personService")
@Transactional
class PersonServiceImpl extends PersonService {

  @Autowired
  var federationService: FederationService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personDao: PersonDao = _

  def find(personId: Long): Option[Person] = {
    personDao.findById(personId)
  }

  def findAllByFederationId(fedId: Long): Seq[Person] = {
    personDao.findAllByFederationId(fedId)
  }

  def findByName(name: String): Seq[Person] = {
    personDao.findByName(name)
  }

  def findByEmail(email: String): Seq[Person] = {
    personDao.findByEmail(email)
  }

  def findByCardId(cardId: String): Seq[Person] = {
    personDao.findByCardId(cardId)
  }

  @throws[InstanceNotFoundException]
  def changePrivacy(personId: Long, newState: Boolean, newAlias: String): Person = {
    personDao.findById(personId).map { person =>
      person.alias = newAlias
      personDao.save(person)
      person
    }.getOrElse(throw new InstanceNotFoundException(personId, classOf[Person].getName()))
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createPerson(stName: String, stSurname: String,
    stEmail: String, stTelephones: String,
    stCardId: String, stBirth: Calendar, idFederation: Long): Person = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurname, stEmail, stTelephones, stBirth, stCardId)

    val maybeFederation: Option[Federation] = federationService.find(idFederation)

    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
        val person: Person = new Person(stName, stSurname, stEmail, stTelephones,
          stCardId, stBirth, stFederation)

        personDao.save(person)
        person
      }
    }
  }

  @throws[IllegalArgumentException]
  def modifyPerson(personId: Long, stName: String, stSurname: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stCardId: String, stBirth: Calendar): Option[Person] = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurname, stEmail, stTelephones, stBirth, stCardId)

    //Modify address
    val maybePerson: Option[Person] = assignAddress(personId, stAddress)

    maybePerson.map {
      person =>
        {
          person.name = stName
          person.surname = stSurname
          person.email = stEmail
          person.avatarLink = new Gravatar(if (Option(stEmail).isEmpty) "" else stEmail).ssl(true).avatarUrl
          person.telephones = stTelephones
          person.cardId = stCardId
          person.birthdate = stBirth
          personDao.save(person)
        }
    }
    maybePerson
  }

  def assignAddress(personId: Long, stAddress: Address): Option[Person] = {

    val newAddress: Address = if (Option(stAddress).nonEmpty) stAddress else new Address()
    val maybePerson: Option[Person] = find(personId)

    maybePerson.map {
      person =>
        {
          if (person.address == stAddress)
            return maybePerson

          if (!Option(person.address).isEmpty)
            addressService.removeAddress(person.address.addressId)

          val savedAddress: Address = addressService.createAddress(
            newAddress.firstLine, newAddress.secondLine,
            newAddress.postCode, newAddress.locality,
            newAddress.province, newAddress.country
          )

          person.address = savedAddress
          personDao.save(person)
        }
    }
    maybePerson
  }

  @throws[IllegalArgumentException]
  protected def checkParameters(stName: String, stSurname: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stCardId: String) {

    val checkAgainstNull = List(
      ("Name", stName, classOf[String]),
      ("Surname", stSurname, classOf[String])
    )

    val checkAgainstEmpty = List(
      ("Name", stName, classOf[String]),
      ("Surname", stSurname, classOf[String])
    )

    checkAgainstNull.map {
      case (eltName, elt, cls) =>
        if (Option(elt).isEmpty)
          throw new IllegalArgumentException(eltName + " cannot be null", cls.getName())
    }
    checkAgainstEmpty.map {
      case (eltName, elt, cls) =>
        if (Option(elt).exists(_.trim == ""))
          throw new IllegalArgumentException(eltName + " cannot be empty", cls.getName())
    }
  }

}

