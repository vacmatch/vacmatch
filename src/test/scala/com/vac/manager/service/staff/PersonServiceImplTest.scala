package test.scala.com.vac.manager.service.StaffService

import com.vac.manager.model.staff.Person
import scala.collection.JavaConverters._
import com.vac.manager.service.staff.PersonService
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.federation.Federation
import org.scalatest.Matchers._
import com.vac.manager.service.federation.FederationService
import com.vac.manager.service.federation.FederationServiceImpl
import com.vac.manager.service.staff.PersonServiceImpl
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import com.vac.manager.service.federation.FederationServiceImpl
import org.scalacheck.Prop.forAll
import org.scalacheck.Arbitrary._
import org.mockito.Mockito._
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfter
import org.mockito.Mockito
import org.mockito.Matchers._
import java.util.Collections
import com.vac.manager.model.staff.PersonDao
import com.vac.manager.service.personal.AddressService

class PersonServiceImplTest
  extends FeatureSpec
  with GivenWhenThen
  with MockitoSugar
  with GeneratorDrivenPropertyChecks
  with BeforeAndAfter {

  /* Generic variables */
  var personService: PersonServiceImpl = _
  var validPerson: Person = _
  var validAddress: Address = _
  var validFederation: Federation = _

  /**
   * Generic variables initialization
   */
  before {
    //Initialization from service to be tested
    personService = new PersonServiceImpl
    personService.federationService = mock[FederationServiceImpl]
    personService.personDao = mock[PersonDao]
    personService.addressService = mock[AddressService]

    //Initialization of a valid Person
    validFederation = new Federation
    validFederation.fedId = 1
    validAddress = new Address
    validAddress.addressId = 1

    validPerson = new Person(
      "Jose", "López Castro", "jlcastro@email.com", "666555444",
      "33442212X", Calendar.getInstance(), validFederation)
    validPerson.address = validAddress
    validPerson.personId = 1

  }

  feature("Person creation") {
    scenario("Person can be created if federation exists and parameters are valid") {

      Given("Valid Person parameters")
      Given("An existent federation")
      val person: Person = validPerson
      //Set default creation address
      person.address = null
      val federation: Federation = validFederation

      Mockito.when(personService.federationService.find(federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      val insertedPerson: Person =
        personService.createPerson(
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.cardId,
          person.birth,
          person.federation.getFedId)

      //Set ID to be the same
      person.personId = insertedPerson.personId

      Then("The Staff gets created")
      Then("The Staff has the same parameters")
      insertedPerson should equal(person)

    }

    scenario("Person cannot be created if federation doesn't exist") {

      Given("Valid Person parameters")
      Given("A not existent federation")
      val person: Person = validPerson
      val notExistentFedId: Long = 2

      Mockito.when(personService.federationService find (notExistentFedId)).thenReturn(None)

      When("A new Person is created")

      intercept[InstanceNotFoundException] {
        val insertedStaff: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            notExistentFedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person name is empty") {

      Given("Empty Person name")
      val person: Person = validPerson
      person.name = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("name parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")
    }

    scenario("Person cannot be created if Person name is null") {

      Given("Null Person name")
      val person: Person = validPerson
      person.name = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("name parameter is null")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person surnames are empty") {

      Given("Empty Person surnames")
      val person: Person = validPerson
      person.surnames = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("surnames parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person surnames are null") {

      Given("Null Person surnames")
      val person: Person = validPerson
      person.surnames = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("surnames parameter is null")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person cardId is empty") {

      Given("Empty Person cardId")
      val person: Person = validPerson
      person.cardId = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("cardId parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person cardId is null") {

      Given("Null Person cardId")
      val person: Person = validPerson
      person.cardId = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("cardId parameter is null")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

    scenario("Person cannot be created if Person birth is null") {

      Given("Null Person birth")
      val person: Person = validPerson
      person.birth = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(personService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new Person is created")
      When("birth parameter is null")

      intercept[IllegalArgumentException] {
        val insertedPerson: Person =
          personService.createPerson(
            person.name,
            person.surnames,
            person.email,
            person.telephones,
            person.cardId,
            person.birth,
            person.federation.fedId)
      }

      Then("The Person cannot be created")

    }

  }

  feature("Person modification") {
    scenario("Someone try to modify a existent Person with valid parameters") {

      Given("An existent Person")
      Given("Valid Person parameters")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(anyLong)).thenReturn(Some(person))

      Given("An existent Federation")
      val federation: Federation = validFederation

      Given("A valid Person Address")
      val address: Address = new Address("Calle Olmos", "11 3º", "codigopostal", "provincia", "ciudad", "pais")

      Mockito.when(personService.addressService.createAddress(
        address.firstLine, address.secondLine, address.postCode,
        address.province, address.locality, address.country)).thenReturn(address)

      When("Person try to be modified")
      val maybePerson: Option[Person] =
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          address,
          person.cardId,
          person.birth)

      Then("Person must be modified")
      val modifiedPerson: Person = maybePerson.get
      modifiedPerson should equal(validPerson)

      Then("Person Address must be created")
      verify(personService.addressService).
        createAddress(
          address.firstLine,
          address.secondLine,
          address.postCode,
          address.locality,
          address.province,
          address.country)

      Then("Person must be saved")
      Then("Person address must be saved")
      verify(personService.personDao, times(2)).save(person)

    }

    scenario("Someone try to modify a not existent Person") {

      Given("Valid Person parameters")
      Given("A not existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(None)

      When("Person try to be modified")
      val modifiedPerson: Option[Person] =
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)

      Then("Person can't be modified")
      modifiedPerson should equal(None)

      Then("Person can't be saved")
      verify(personService.personDao, never).save(person)

    }

    scenario("Someone try to modify a existent Person with null name") {

      Given("An existent Person")
      Given("Null Person name")
      val person: Person = validPerson
      person.name = null

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("name is null")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with empty name") {

      Given("An existent Person")
      Given("Empty Person name")
      val person: Person = validPerson
      person.name = ""

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("name is empty")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with null surnames") {

      Given("An existent Person")
      Given("Null Person surnames")
      val person: Person = validPerson
      person.surnames = null

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("surnames are null")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with empty surnames") {

      Given("An existent Person")
      Given("Empty Person surnames")
      val person: Person = validPerson
      person.surnames = ""

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("surnames are empty")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with null cardId") {

      Given("An existent Person")
      Given("Null Person cardId")
      val person: Person = validPerson
      person.cardId = null

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("cardId is null")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with empty cardId") {

      Given("An existent Person")
      Given("Empty Person cardId")
      val person: Person = validPerson
      person.cardId = ""

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("cardId is empty")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }

    scenario("Someone try to modify a existent Person with null birth") {

      Given("An existent Person")
      Given("Null Person birth")
      val person: Person = validPerson
      person.birth = null

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      When("Person try to be modified")
      When("birth is null")
      intercept[IllegalArgumentException] {
        personService.modifyPerson(
          person.personId,
          person.name,
          person.surnames,
          person.email,
          person.telephones,
          person.address,
          person.cardId,
          person.birth)
      }

      Then("Person can't be modified")

    }
  }

  feature("Person address modification") {
    scenario("A Person address must be modified when old address is not null") {

      Given("An existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      Given("A Person old address")
      val oldAddress = validAddress
      person.address = oldAddress

      Given("Valid new Address")
      val newAddress: Address = new Address("Plaza Pontevedra", "21 1ºA", "15004", "A Coruña",
        "A Coruña", "España")
      newAddress.addressId = 1

      Mockito.when(personService.addressService.createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)).thenReturn(newAddress)

      When("Person address try to be modified")
      val modifiedPerson: Option[Person] =
        personService.assignAddress(person.personId, newAddress)

      Then("Old Person address must be removed from de DB")
      verify(personService.addressService).removeAddress(oldAddress.addressId)

      Then("Person must be modified")
      modifiedPerson.get.address should equal(newAddress)

    }

    scenario("A Person address must be modified when old address is null") {

      Given("An existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      Given("A null Person old address")
      val oldAddress: Address = null
      person.address = oldAddress

      Given("Valid new Address")
      val newAddress: Address = validAddress

      Mockito.when(personService.addressService.createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)).thenReturn(newAddress)

      When("Person address try to be modified")
      val modifiedPerson: Option[Person] =
        personService.assignAddress(person.personId, newAddress)

      Then("Old Person address shouldn't be removed from de DB")
      verify(personService.addressService, never).removeAddress(anyLong)

      Then("Person must be modified")
      modifiedPerson.get.address should equal(newAddress)

    }

    scenario("A Person address can't be modified if Person doesn't exist") {
      Given("A not existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(None)

      Given("A Person old address")
      val oldAddress: Address = validAddress
      person.address = oldAddress

      Given("Valid new Address")
      val newAddress: Address = new Address("Calle Orzan", "12 5º", "15003",
        "A Coruña", "A Coruña", "España")

      When("Person address try to be modified")
      val modifiedPerson: Option[Person] =
        personService.assignAddress(person.personId, newAddress)

      Then("Old Person address shouldn't be removed from de DB")
      verify(personService.addressService, never).removeAddress(anyLong)

      Then("New address can't be saved in the DB")
      verify(personService.addressService, never).createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)

      Then("Person can't be modified")
      modifiedPerson should equal(None)

    }

    scenario("A Person address can't be modified if new Address is null") {

      Given("An existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      Given("A Person old address")
      val oldAddress: Address = validAddress
      person.address = oldAddress

      Given("A null new Address")
      val newAddress: Address = null

      When("Person address try to be modified")
      val modifiedPerson: Option[Person] =
        personService.assignAddress(person.personId, newAddress)

      Then("Old Person address shouldn't be removed from de DB")
      verify(personService.addressService, never).removeAddress(oldAddress.addressId)

      Then("New address can't be saved in the DB")
      verify(personService.addressService, never).createAddress(
        anyString, anyString, anyString, anyString, anyString, anyString)

      Then("Person can't be modified")
      modifiedPerson.get.address should equal(oldAddress)

    }

    scenario("A Person address can't be modified if new Address don't change") {

      Given("An existent Person")
      val person: Person = validPerson

      Mockito.when(personService.personDao.findById(person.personId)).thenReturn(Some(person))

      Given("A Person old address")
      val oldAddress: Address = validAddress
      person.address = oldAddress

      Given("The same new Address than old Person address")
      val newAddress: Address = oldAddress

      When("Person address try to be modified")
      val modifiedPerson: Option[Person] =
        personService.assignAddress(person.personId, newAddress)

      Then("Old Person address shouldn't be removed from de DB")
      verify(personService.addressService, never).removeAddress(oldAddress.addressId)

      Then("New address can't be saved in the DB")
      verify(personService.addressService, never).createAddress(
        anyString, anyString, anyString, anyString, anyString, anyString)

      Then("Person can't be modified")
      modifiedPerson.get.address should equal(oldAddress)
    }

  }
}

