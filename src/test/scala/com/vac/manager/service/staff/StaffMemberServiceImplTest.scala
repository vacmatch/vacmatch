package test.scala.com.vac.manager.service.StaffService

import com.vac.manager.model.staff.StaffMember
import scala.collection.JavaConverters._
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.federation.Federation
import org.scalatest.Matchers._
import com.vac.manager.service.federation.FederationService
import com.vac.manager.service.federation.FederationServiceImpl
import com.vac.manager.service.staff.StaffMemberServiceImpl
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
import com.vac.manager.model.staff.StaffMemberDao
import com.vac.manager.service.personal.AddressService

class StaffMemberServiceImplTest
  extends FeatureSpec
  with GivenWhenThen
  with MockitoSugar
  with GeneratorDrivenPropertyChecks
  with BeforeAndAfter {

  /* Generic variables */
  var staffMemberService: StaffMemberServiceImpl = _
  var validStaffMember: StaffMember = _
  var validAddress: Address = _
  var validFederation: Federation = _

  /**
   * Generic variables initialization
   */
  before {
    //Initialization from service to be tested
    staffMemberService = new StaffMemberServiceImpl
    staffMemberService.federationService = mock[FederationServiceImpl]
    staffMemberService.staffMemberDao = mock[StaffMemberDao]
    staffMemberService.addressService = mock[AddressService]

    //Initialization of a valid StaffMember
    validFederation = new Federation
    validFederation.fedId = 1
    validAddress = new Address
    validAddress.addressId = 1

    validStaffMember = new StaffMember(
      "Jose", "López Castro", "jlcastro@email.com", "666555444",
      "33442212X", Calendar.getInstance(), validFederation)
    validStaffMember.staffAddress = validAddress
    validStaffMember.staffId = 1

  }

  feature("StaffMember creation") {
    scenario("StaffMember can be created if federation exists and parameters are valid") {

      Given("Valid StaffMember parameters")
      Given("An existent federation")
      val staffMember: StaffMember = validStaffMember
      //Set default creation address
      staffMember.staffAddress = null
      val federation: Federation = validFederation

      Mockito.when(staffMemberService.federationService.find(federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      val insertedStaffMember: StaffMember =
        staffMemberService.createStaff(
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffCardId,
          staffMember.staffBirth,
          staffMember.staffFederation.getFedId)

      //Set ID to be the same
      staffMember.staffId = insertedStaffMember.staffId

      Then("The Staff gets created")
      Then("The Staff has the same parameters")
      insertedStaffMember should equal(staffMember)

    }

    scenario("StaffMember cannot be created if federation doesn't exist") {

      Given("Valid StaffMember parameters")
      Given("A not existent federation")
      val staffMember: StaffMember = validStaffMember
      val notExistentFedId: Long = 2

      Mockito.when(staffMemberService.federationService find (notExistentFedId)).thenReturn(None)

      When("A new StaffMember is created")

      intercept[InstanceNotFoundException] {
        val insertedStaff: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            notExistentFedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember name is empty") {

      Given("Empty StaffMember name")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffName = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffName parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")
    }

    scenario("StaffMember cannot be created if StaffMember name is null") {

      Given("Null StaffMember name")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffName = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffName parameter is null")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember surnames are empty") {

      Given("Empty StaffMember surnames")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffSurnames = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffSurnames parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember surnames are null") {

      Given("Null StaffMember surnames")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffSurnames = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffSurnames parameter is null")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember cardId is empty") {

      Given("Empty StaffMember cardId")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffCardId = ""

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffCardId parameter is empty")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember cardId is null") {

      Given("Null StaffMember cardId")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffCardId = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffCardId parameter is null")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

    scenario("StaffMember cannot be created if StaffMember staffBirth is null") {

      Given("Null StaffMember birth")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffBirth = null

      Given("An existent federation")
      val federation: Federation = validFederation
      Mockito.when(staffMemberService.federationService find (federation.fedId)).thenReturn(Some(federation))

      When("A new StaffMember is created")
      When("staffBirth parameter is null")

      intercept[IllegalArgumentException] {
        val insertedStaffMember: StaffMember =
          staffMemberService.createStaff(
            staffMember.staffName,
            staffMember.staffSurnames,
            staffMember.staffEmail,
            staffMember.staffTelephones,
            staffMember.staffCardId,
            staffMember.staffBirth,
            staffMember.staffFederation.fedId)
      }

      Then("The StaffMember cannot be created")

    }

  }

  feature("StaffMember activation") {
    scenario("StaffMember activation can be changed when StaffMember exists and parameters are valid") {
      Given("A staffMember and a new activation state")
      var staffMember: StaffMember = new StaffMember()
      var newState: Boolean = true

      When("Federation exists")
      When("StaffMember exists")
      When("StaffMember activation state is valid")

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(Some(staffMember))

      staffMemberService.changeActivation(anyLong, newState)

      Then("StaffMember activation has been changed")
      Then("StaffMember activation has the new state")

      staffMember.staffActivated should equal(newState)
      verify(staffMemberService.staffMemberDao).save(staffMember)

    }

    scenario("StaffMember activation cannot be changed when StaffMember doesn't exist") {
      Given("A staffMember, an old activation state and a new activation state")
      var staffMember: StaffMember = new StaffMember()
      var oldState: Boolean = false
      var newState: Boolean = true

      When("Federation exists")
      When("StaffMember doesn't exist")
      When("StaffMember activation state is valid")

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(None)

      intercept[InstanceNotFoundException] {
        staffMemberService.changeActivation(anyLong, newState)
      }

      Then("StaffMember activation hasn't been changed")

      assert(staffMember.staffActivated == oldState)
      verify(staffMemberService.staffMemberDao, never).save(staffMember)
    }
  }

  feature("StaffMember modification") {
    scenario("Someone try to modify a existent StaffMember with valid parameters") {

      Given("An existent StaffMember")
      Given("Valid StaffMember parameters")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(Some(staffMember))

      Given("An existent Federation")
      val federation: Federation = validFederation

      Given("A valid StaffMember Address")
      val address: Address = new Address("Calle Olmos", "11 3º", "codigopostal", "provincia", "ciudad", "pais")

      Mockito.when(staffMemberService.addressService.createAddress(
        address.firstLine, address.secondLine, address.postCode,
        address.province, address.locality, address.country)).thenReturn(address)

      When("StaffMember try to be modified")
      val maybeStaffMember: Option[StaffMember] =
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          address,
          staffMember.staffCardId,
          staffMember.staffBirth)

      Then("StaffMember must be modified")
      val modifiedStaffMember: StaffMember = maybeStaffMember.get
      modifiedStaffMember should equal(validStaffMember)

      Then("StaffMember Address must be created")
      verify(staffMemberService.addressService).
        createAddress(
          address.firstLine,
          address.secondLine,
          address.postCode,
          address.locality,
          address.province,
          address.country)

      Then("StaffMember must be saved")
      Then("StaffMember address must be saved")
      verify(staffMemberService.staffMemberDao, times(2)).save(staffMember)

    }

    scenario("Someone try to modify a not existent StaffMember") {

      Given("Valid StaffMember parameters")
      Given("A not existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(None)

      When("StaffMember try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)

      Then("StaffMember can't be modified")
      modifiedStaffMember should equal(None)

      Then("StaffMember can't be saved")
      verify(staffMemberService.staffMemberDao, never).save(staffMember)

    }

    scenario("Someone try to modify a existent StaffMember with null name") {

      Given("An existent StaffMember")
      Given("Null StaffMember name")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffName = null

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffName is null")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with empty name") {

      Given("An existent StaffMember")
      Given("Empty StaffMember name")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffName = ""

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffName is empty")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with null surnames") {

      Given("An existent StaffMember")
      Given("Null StaffMember surnames")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffSurnames = null

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffSurnames are null")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with empty surnames") {

      Given("An existent StaffMember")
      Given("Empty StaffMember surnames")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffSurnames = ""

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffSurnames are empty")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with null cardId") {

      Given("An existent StaffMember")
      Given("Null StaffMember cardId")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffCardId = null

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffCardId is null")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with empty cardId") {

      Given("An existent StaffMember")
      Given("Empty StaffMember cardId")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffCardId = ""

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffCardId is empty")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }

    scenario("Someone try to modify a existent StaffMember with null birth") {

      Given("An existent StaffMember")
      Given("Null StaffMember birth")
      val staffMember: StaffMember = validStaffMember
      staffMember.staffBirth = null

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      When("StaffMember try to be modified")
      When("staffBirth is null")
      intercept[IllegalArgumentException] {
        staffMemberService.modifyStaff(
          staffMember.staffId,
          staffMember.staffName,
          staffMember.staffSurnames,
          staffMember.staffEmail,
          staffMember.staffTelephones,
          staffMember.staffAddress,
          staffMember.staffCardId,
          staffMember.staffBirth)
      }

      Then("StaffMember can't be modified")

    }
  }

  feature("StaffMember address modification") {
    scenario("A StaffMember address must be modified when old address is not null") {

      Given("An existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      Given("A StaffMember old address")
      val oldAddress = validAddress
      staffMember.staffAddress = oldAddress

      Given("Valid new Address")
      val newAddress: Address =  new Address("Plaza Pontevedra", "21 1ºA", "15004", "A Coruña",
        "A Coruña", "España")
      newAddress.addressId = 1

      Mockito.when(staffMemberService.addressService.createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)).thenReturn(newAddress)

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.assignAddress(staffMember.staffId, newAddress)

      Then("Old StaffMember address must be removed from de DB")
      verify(staffMemberService.addressService).removeAddress(oldAddress.addressId)

      Then("StaffMember must be modified")
      modifiedStaffMember.get.staffAddress should equal(newAddress)

    }

    scenario("A StaffMember address must be modified when old address is null") {

      Given("An existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      Given("A null StaffMember old address")
      val oldAddress: Address = null
      staffMember.staffAddress = oldAddress

      Given("Valid new Address")
      val newAddress: Address = validAddress

      Mockito.when(staffMemberService.addressService.createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)).thenReturn(newAddress)

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.assignAddress(staffMember.staffId, newAddress)

      Then("Old StaffMember address shouldn't be removed from de DB")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)

      Then("StaffMember must be modified")
      modifiedStaffMember.get.staffAddress should equal(newAddress)

    }

    scenario("A StaffMember address can't be modified if StaffMember doesn't exist") {
      Given("A not existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(None)

      Given("A StaffMember old address")
      val oldAddress: Address = validAddress
      staffMember.staffAddress = oldAddress

      Given("Valid new Address")
      val newAddress: Address = new Address("Calle Orzan", "12 5º", "15003",
        "A Coruña", "A Coruña", "España")

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.assignAddress(staffMember.staffId, newAddress)

      Then("Old StaffMember address shouldn't be removed from de DB")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)

      Then("New address can't be saved in the DB")
      verify(staffMemberService.addressService, never).createAddress(
        newAddress.firstLine, newAddress.secondLine, newAddress.postCode,
        newAddress.locality, newAddress.province, newAddress.country)

      Then("StaffMember can't be modified")
      modifiedStaffMember should equal(None)

    }

    scenario("A StaffMember address can't be modified if new Address is null") {

      Given("An existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      Given("A StaffMember old address")
      val oldAddress: Address = validAddress
      staffMember.staffAddress = oldAddress

      Given("A null new Address")
      val newAddress: Address = null

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.assignAddress(staffMember.staffId, newAddress)

      Then("Old StaffMember address shouldn't be removed from de DB")
      verify(staffMemberService.addressService, never).removeAddress(oldAddress.addressId)

      Then("New address can't be saved in the DB")
      verify(staffMemberService.addressService, never).createAddress(
        anyString, anyString, anyString, anyString, anyString, anyString)

      Then("StaffMember can't be modified")
      modifiedStaffMember.get.staffAddress should equal(oldAddress)

    }

    scenario("A StaffMember address can't be modified if new Address don't change") {

      Given("An existent StaffMember")
      val staffMember: StaffMember = validStaffMember

      Mockito.when(staffMemberService.staffMemberDao.findById(staffMember.staffId)).thenReturn(Some(staffMember))

      Given("A StaffMember old address")
      val oldAddress: Address = validAddress
      staffMember.staffAddress = oldAddress

      Given("The same new Address than old StaffMember address")
      val newAddress: Address = oldAddress

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] =
        staffMemberService.assignAddress(staffMember.staffId, newAddress)

      Then("Old StaffMember address shouldn't be removed from de DB")
      verify(staffMemberService.addressService, never).removeAddress(oldAddress.addressId)

      Then("New address can't be saved in the DB")
      verify(staffMemberService.addressService, never).createAddress(
        anyString, anyString, anyString, anyString, anyString, anyString)

      Then("StaffMember can't be modified")
      modifiedStaffMember.get.staffAddress should equal(oldAddress)
    }

  }
}




