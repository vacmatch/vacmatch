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

class StaffMemberServiceTest 
				extends FeatureSpec
				with GivenWhenThen
				with MockitoSugar
				with GeneratorDrivenPropertyChecks 
				with BeforeAndAfter {

  /* Generic variables */
  var validStaffMember: StaffMember = _
  var mockAddress: Address = mock[Address]
  var mockFederation: Federation = mock[Federation]
  
  /*
   * Auxiliar function for checking if parameters are legal or not
   */
  def validParameters(staffName: String, staffSurnames: Array[String],
    staffEmail: String, staffTelephones: Array[String], staffNif: String): Boolean = {
    
    ! ((staffName.isEmpty()) || (staffSurnames.isEmpty) || (staffEmail.isEmpty) 
        || (staffTelephones.isEmpty) || (staffNif.isEmpty()))
  }
  
  /**
   * Generic variables initialization
   */
  before {
    validStaffMember = new StaffMember(
    "Jose", "López Castro", "jlcastro@email.com", "666555444", mockAddress, 
    "33442212X", Calendar.getInstance(), mockFederation)
  }
  
  
  feature("StaffMember creation") {
    scenario("StaffMember can be created if federation exists and parameters are valid"){
      Given("A federation")
      var fedId: Int = 1

      //Service to be tested
      var staffMemberService: StaffMemberServiceImpl = new StaffMemberServiceImpl
      
      staffMemberService.federationService = mock[FederationServiceImpl]
      staffMemberService.staffMemberDao = mock[StaffMemberDao]
      
      When("A new StaffMember is created")
      When("StaffMember parameters are valid")
      When("Federation exists")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffMemberService.federationService find(fedId)) thenReturn Some(mockFederation)

      var insertedStaffMember: StaffMember = staffMemberService.createStaff(
        validStaffMember.staffName, validStaffMember.staffSurnames ,
        validStaffMember.staffEmail, validStaffMember.staffTelephones, 
        validStaffMember.staffAddress, validStaffMember.staffNif,
        validStaffMember.staffBirth, validStaffMember.staffFederation.getFedId)
      
      Then("The Staff gets created")
      Then("The Staff has the same parameters")
      
      insertedStaffMember should equal (validStaffMember)
      
    }
    
    scenario("StaffMember cannot be created if federation doesn't exist"){
      Given("A not existent federation")
      var fedId: Int = 2

      //Service to be tested
      var staffMemberService: StaffMemberServiceImpl = new StaffMemberServiceImpl
      
      staffMemberService.federationService = mock[FederationServiceImpl]
      staffMemberService.staffMemberDao = mock[StaffMemberDao]
      
      When("A new StaffMember is created")
      When("StaffMember parameters are valid")
      When("Federation doesn't exist")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffMemberService.federationService find(fedId)) thenReturn None

      intercept[InstanceNotFoundException]{
        var insertedStaff: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          validStaffMember.staffBirth, fedId)
      }
      
      Then("The StaffMember cannot be created")
      
    }

    scenario("StaffMember cannot be created if parameters are not valid"){
      Given("A federation")
      var fedId: Int = 1

      //Service to be tested
      var staffMemberService: StaffMemberServiceImpl = new StaffMemberServiceImpl
      
      staffMemberService.federationService = mock[FederationServiceImpl]
      staffMemberService.staffMemberDao = mock[StaffMemberDao]
      
      When("A new StaffMember is created")
      When("Federation exists")
      
      When("staffName parameter is empty")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffMemberService.federationService find(fedId)) thenReturn Some(mockFederation)

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          "", validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffName parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          null, validStaffMember.staffSurnames ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffSurnames parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, "" ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffSurnames parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, null ,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffNif parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress,"",
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffNif parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, null,
          validStaffMember.staffBirth, validStaffMember.staffFederation.fedId)
      }

      When("staffBirth parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaffMember: StaffMember = staffMemberService.createStaff(
          validStaffMember.staffName, validStaffMember.staffSurnames,
          validStaffMember.staffEmail, validStaffMember.staffTelephones, 
          validStaffMember.staffAddress, validStaffMember.staffNif,
          null, validStaffMember.staffFederation.fedId)
      }
      
      Then("The StaffMember cannot be created")
      
    }
    
  }
  
  feature("StaffMember activation") {
    scenario("StaffMember activation can be changed when StaffMember exists and parameters are valid") {
      Given("A staffMember and a new activation state")
      var staffMember: StaffMember = new StaffMember
      var newState: Boolean = true

      //Service to be tested
      var staffMemberService: StaffMemberServiceImpl = new StaffMemberServiceImpl

      staffMemberService.federationService = mock[FederationServiceImpl]
      staffMemberService.staffMemberDao = mock[StaffMemberDao]

      When("Federation exists")
      When("StaffMember exists")
      When("StaffMember activation state is valid")
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn staffMember

      staffMemberService.changeActivation(anyLong, newState)

      Then("StaffMember activation has been changed")
      Then("StaffMember activation has the new state")
      
      assert(staffMember.staffActivated == newState)
      verify(staffMemberService staffMemberDao) save(staffMember)

    }
    
    scenario("StaffMember activation cannot be changed when StaffMember doesn't exist") {
      Given("A staffMember, an old activation state and a new activation state")
      var staffMember: StaffMember = new StaffMember
      var oldState: Boolean = false
      var newState: Boolean = true

      //Service to be tested
      var staffMemberService: StaffMemberServiceImpl = new StaffMemberServiceImpl

      staffMemberService.federationService = mock[FederationServiceImpl]
      staffMemberService.staffMemberDao = mock[StaffMemberDao]

      When("Federation exists")
      When("StaffMember doesn't exist")
      When("StaffMember activation state is valid")
      
      Mockito.when(staffMemberService.staffMemberDao.findById(anyLong)) thenReturn null

      intercept[InstanceNotFoundException] {
        staffMemberService.changeActivation(anyLong, newState)
      }
      
      Then("StaffMember activation hasn't been changed")

      assert(staffMember.staffActivated == oldState)
      verify(staffMemberService staffMemberDao, never) save(staffMember)
    }
  }
}


