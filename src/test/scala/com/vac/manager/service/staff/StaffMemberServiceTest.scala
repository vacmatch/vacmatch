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

class StaffMemberServiceTest 
				extends FeatureSpec
				with GivenWhenThen
				with MockitoSugar
				with GeneratorDrivenPropertyChecks 
				with BeforeAndAfter {

  /* Generic variables */
  var staffMemberService: StaffMemberServiceImpl = _
  var validStaffMember: StaffMember = _
  var mockAddress: Address = _
  var mockFederation: Federation = _
  
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
    mockAddress = mock[Address]
    mockFederation = mock[Federation]

    mockFederation.fedId = 1
    validStaffMember = new StaffMember(
    "Jose", "López Castro", "jlcastro@email.com", "666555444", 
    "33442212X", Calendar.getInstance(), mockFederation)

  }
  
  
  feature("StaffMember creation") {
    scenario("StaffMember can be created if federation exists and parameters are valid"){
      Given("A federation")
      var fedId: Int = 1

      When("A new StaffMember is created")
      When("StaffMember parameters are valid")
      When("Federation exists")

      Mockito.when(mockFederation.getFedId).thenReturn(fedId)
	  Mockito.when(staffMemberService.federationService.find(fedId)).thenReturn(Some(mockFederation))

      var insertedStaffMember: StaffMember = staffMemberService.createStaff(
        validStaffMember.staffName, validStaffMember.staffSurnames ,
        validStaffMember.staffEmail, validStaffMember.staffTelephones, 
        validStaffMember.staffCardId, validStaffMember.staffBirth, 
        validStaffMember.staffFederation.getFedId)
      
      Then("The Staff gets created")
      Then("The Staff has the same parameters")
      
      insertedStaffMember should equal (validStaffMember)
      
    }
    
    scenario("StaffMember cannot be created if federation doesn't exist"){
      Given("A not existent federation")
      var fedId: Int = 2

      When("A new StaffMember is created")
      When("StaffMember parameters are valid")
      When("Federation doesn't exist")

      Mockito.when(mockFederation.getFedId).thenReturn(fedId)
	  Mockito.when(staffMemberService.federationService find(fedId)) thenReturn None

      intercept[InstanceNotFoundException]{
        var insertedStaff: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, validStaffMember.staffBirth, fedId)
      }
      
      Then("The StaffMember cannot be created")
      
    }

    scenario("StaffMember cannot be created if parameters are not valid"){
      Given("A federation")
      var fedId: Int = 1

      When("A new StaffMember is created")
      When("Federation exists")
      
      When("staffName parameter is empty")

      Mockito.when(mockFederation.getFedId).thenReturn(fedId)
	  Mockito.when(staffMemberService.federationService find(fedId)) thenReturn Some(mockFederation)

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          "", validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, validStaffMember.staffBirth, 
          validStaffMember.staffFederation.fedId)
      }

      When("staffName parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          null, validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, validStaffMember.staffBirth,
          validStaffMember.staffFederation.fedId)
      }

      When("staffSurnames parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, "" ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, validStaffMember.staffBirth, 
          validStaffMember.staffFederation.fedId)
      }

      When("staffSurnames parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, null ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, validStaffMember.staffBirth, 
          validStaffMember.staffFederation.fedId)
      }

      When("staffCardId parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          "", validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffCardId parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          null, validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffBirth parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffCardId, null, validStaffMember.staffFederation.fedId)
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
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn staffMember

      staffMemberService.changeActivation(anyLong, newState)

      Then("StaffMember activation has been changed")
      Then("StaffMember activation has the new state")
      
      staffMember.staffActivated should equal (newState)
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
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn null

      intercept[InstanceNotFoundException] {
        staffMemberService.changeActivation(anyLong, newState)
      }
      
      Then("StaffMember activation hasn't been changed")

      assert(staffMember.staffActivated == oldState)
      verify(staffMemberService.staffMemberDao, never).save(staffMember)
    }
  }
  
  
  feature("StaffMember modification"){
    scenario("Someone try to modify a existent StaffMember with valid parameters"){

      Given("An existent StaffMember")
      Given("An existent Federation")
      Given("A null StaffMember Address")
      Given("Valid StaffMember parameters")
      var staffMember: StaffMember = new StaffMember("Pablo","Castro","email@e.com",
         "999888222","33445566x",Calendar.getInstance(),mockFederation)
      var staffMemberId: Long = 1

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(staffMember)
      Mockito.when(staffMemberService.addressService.createAddress(
          anyString, anyString, anyString, anyString, anyString)).thenReturn(mockAddress)
      
      When("StaffMember try to be modified")
      var maybeStaffMember: Option[StaffMember] = 
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, validStaffMember.staffSurnames, 
          validStaffMember.staffEmail, validStaffMember.staffTelephones,
          validStaffMember.staffAddress, validStaffMember.staffCardId,
          validStaffMember.staffBirth)

      val modifiedStaffMember: StaffMember = maybeStaffMember.get

      Then("StaffMember must be modified")
      Then("StaffMember Address must be saved")
      Then("StaffMember must be saved")
      modifiedStaffMember should equal (validStaffMember)
      verify(staffMemberService.staffMemberDao).save(staffMember)

    }

    scenario("Someone try to modify a not existent StaffMember"){
      
      Given("A not existent StaffMember")
      Given("Valid StaffMember parameters")
      var staffMember: StaffMember = new StaffMember()
      var staffMemberId: Long = 1

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn null
      
      When("StaffMember try to be modified")
      var modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, validStaffMember.staffBirth)
      
      Then("StaffMember can't be modified")
      Then("StaffMember can't be saved")
      modifiedStaffMember should equal (None)
      verify(staffMemberService.staffMemberDao, never).save(staffMember)

    }

    scenario("Someone try to modify a existent StaffMember with not valid parameters"){
      
      Given("An existent StaffMember")
      Given("Not valid StaffMember parameter")
      var staffMember: StaffMember = new StaffMember()
      var staffMemberId: Long = 1
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn staffMember

      When("staffName is null")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          null, 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, validStaffMember.staffBirth)
      }

      When("staffName is empty")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          "", 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, validStaffMember.staffBirth)
      }
      
      When("staffSurnames are null")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          null, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, validStaffMember.staffBirth)
      }
      
      When("staffSurnames are empty")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          "", validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, validStaffMember.staffBirth)
      }

      When("staffCardId is null")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          null, validStaffMember.staffBirth)
      }
      
      When("staffCardId is empty")
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          "", validStaffMember.staffBirth)
      }

      When("staffBirth parameter is null")
      
      intercept[IllegalArgumentException]{
        staffMemberService.modifyStaff(staffMemberId, 
          validStaffMember.staffName, 
          validStaffMember.staffSurnames, validStaffMember.staffEmail, 
          validStaffMember.staffTelephones , validStaffMember.staffAddress, 
          validStaffMember.staffCardId, null)
      }

      Then("StaffMember can't be modified")

    }
  }
  
  feature("StaffMember address modification"){
    scenario("A StaffMember address must be modified when old address is not null"){
      
      Given("An existent StaffMember")
      Given("A StaffMember old address")
      Given("Valid new Address")
      val staffMemberId: Long = 1
      validStaffMember.staffAddress = new Address("Plaza Pontevedra 21", "15004", 
          "A Coruña", "A Coruña", "España")
      val newAddress: Address = new Address("Calle Orzan 12 5º", "15003", 
          "A Coruña", "A Coruña", "España")
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(validStaffMember)
      Mockito.when(staffMemberService.addressService.createAddress(
          newAddress.addressLine, newAddress.postCode, newAddress.locality,
          newAddress.province, newAddress.country)).thenReturn(newAddress)
          
      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.assignAddress(staffMemberId, newAddress)
      
      Then("Old StaffMember address must be removed from de DB")
      Then("StaffMember must be modified")
      verify(staffMemberService.addressService).removeAddress(anyLong)
      modifiedStaffMember.get.staffAddress should equal (newAddress)
    }

    scenario("A StaffMember address must be modified when old address is null"){
      
      Given("An existent StaffMember")
      Given("A null StaffMember old address")
      Given("Valid new Address")
      val staffMemberId: Long = 1
      val newAddress: Address = new Address("Calle Orzan 12 5º", "15003", 
          "A Coruña", "A Coruña", "España")
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(validStaffMember)
      Mockito.when(staffMemberService.addressService.createAddress(
          newAddress.addressLine, newAddress.postCode, newAddress.locality,
          newAddress.province, newAddress.country)).thenReturn(newAddress)
          
      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.assignAddress(staffMemberId, newAddress)
      
      Then("Old StaffMember address shouldn't be removed from de DB")
      Then("StaffMember must be modified")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)
      modifiedStaffMember.get.staffAddress should equal (newAddress)
    }

    scenario("A StaffMember address can't be modified if StaffMember doesn't exists"){
      
      Given("A not existent StaffMember")
      Given("A StaffMember old address")
      Given("Valid new Address")
      val staffMemberId: Long = 1
      validStaffMember.staffAddress = new Address("Plaza Pontevedra 21", "15004", 
          "A Coruña", "A Coruña", "España")
      val newAddress: Address = new Address("Calle Orzan 12 5º", "15003", 
          "A Coruña", "A Coruña", "España")
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(null)
      
      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.assignAddress(staffMemberId, newAddress)
        
      Then("Old StaffMember address shouldn't be removed from de DB")
      Then("New address can't be saved in the DB")
      Then("StaffMember can't be modified")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)
      verify(staffMemberService.addressService, never).createAddress(anyString,
          anyString, anyString, anyString, anyString)
      modifiedStaffMember should equal (None)
    }
    
    scenario("A StaffMember address can't be modified if new Address is null"){
      Given("An existent StaffMember")
      Given("A StaffMember old address")
      Given("A null new Address")
      val staffMemberId: Long = 1
      validStaffMember.staffAddress = new Address("Plaza Pontevedra 21", "15004", 
          "A Coruña", "A Coruña", "España")
      val newAddress: Address = null
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(validStaffMember)
      
      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.assignAddress(staffMemberId, newAddress)
      
      Then("Old StaffMember address shouldn't be removed from de DB")
      Then("New address can't be saved in the DB")
      Then("StaffMember can't be modified")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)
      verify(staffMemberService.addressService, never).createAddress(anyString,
          anyString, anyString, anyString, anyString)
      modifiedStaffMember.get.staffAddress should equal (validStaffMember.staffAddress)
    }
    
    scenario("A StaffMember address can't be modified if new Address don't change"){
      
      Given("An existent StaffMember")
      Given("A StaffMember old address")
      Given("The same new Address than old StaffMember address")
      val staffMemberId: Long = 1
      val newAddress: Address = new Address("Plaza Pontevedra 21", "15004", 
          "A Coruña", "A Coruña", "España")	
      validStaffMember.staffAddress = newAddress

      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)).thenReturn(validStaffMember)

      When("StaffMember address try to be modified")
      val modifiedStaffMember: Option[StaffMember] = 
        staffMemberService.assignAddress(staffMemberId, newAddress)
        
      Then("Old StaffMember address shouldn't be removed from de DB")
      Then("New address can't be saved in the DB")
      Then("StaffMember can't be modified")
      verify(staffMemberService.addressService, never).removeAddress(anyLong)
      verify(staffMemberService.addressService, never).createAddress(anyString,
          anyString, anyString, anyString, anyString)
      modifiedStaffMember.get.staffAddress should equal (validStaffMember.staffAddress)
    }

  }
}




