package test.scala.com.vac.manager.service.StaffService

import com.vac.manager.model.staff.Staff
import scala.collection.JavaConverters._
import com.vac.manager.service.staff.StaffService
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.federation.Federation
import org.scalatest.Matchers._
import com.vac.manager.model.staff.StaffDao
import com.vac.manager.service.federation.FederationService
import com.vac.manager.service.federation.FederationServiceImpl
import com.vac.manager.service.staff.StaffServiceImpl
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

class StaffServiceTest 
				extends FeatureSpec
				with GivenWhenThen
				with MockitoSugar
				with GeneratorDrivenPropertyChecks 
				with BeforeAndAfter {

  /* Generic variables */
  var validStaff: Staff = _
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
    validStaff = new Staff(
    "Jose", "López Castro", "jlcastro@email.com", "666555444", mockAddress, 
    "33442212X", Calendar.getInstance(), mockFederation)
  }
  
  
  feature("Staff creation") {
    scenario("Staff can be created if federation exists and parameters are valid"){
      Given("A federation")
      var fedId: Int = 1

      //Service to be tested
      var staffService: StaffServiceImpl = new StaffServiceImpl
      
      staffService.federationService = mock[FederationServiceImpl]
      staffService.staffDao = mock[StaffDao]
      
      When("A new Staff is created")
      When("Staff parameters are valid")
      When("Federation exists")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffService.federationService find(fedId)) thenReturn Some(mockFederation)

      var insertedStaff: Staff = staffService.createStaff(
        validStaff.staffName, validStaff.staffSurnames ,
        validStaff.staffEmail, validStaff.staffTelephones, 
        validStaff.staffAddress, validStaff.staffNif,
        validStaff.staffBirth, validStaff.staffFederation.getFedId)
      
      Then("The Staff gets created")
      Then("The Staff has the same parameters")
      
      insertedStaff should equal (validStaff)
      
    }
    
    scenario("Staff cannot be created if federation doesn't exist"){
      Given("A not existent federation")
      var fedId: Int = 2

      //Service to be tested
      var staffService: StaffServiceImpl = new StaffServiceImpl
      
      staffService.federationService = mock[FederationServiceImpl]
      staffService.staffDao = mock[StaffDao]
      
      When("A new Staff is created")
      When("Staff parameters are valid")
      When("Federation doesn't exist")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffService.federationService find(fedId)) thenReturn None

      intercept[InstanceNotFoundException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, validStaff.staffSurnames ,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          validStaff.staffBirth, fedId)
      }
      
      Then("The Staff cannot be created")
      
    }

    scenario("Staff cannot be created if parameters are not valid"){
      Given("A federation")
      var fedId: Int = 1

      //Service to be tested
      var staffService: StaffServiceImpl = new StaffServiceImpl
      
      staffService.federationService = mock[FederationServiceImpl]
      staffService.staffDao = mock[StaffDao]
      
      When("A new Staff is created")
      When("Federation exists")
      
      When("staffName parameter is empty")

      Mockito.when(mockFederation getFedId) thenReturn fedId
	  Mockito.when(staffService.federationService find(fedId)) thenReturn Some(mockFederation)

      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          "", validStaff.staffSurnames ,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffName parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          null, validStaff.staffSurnames ,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffSurnames parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, "" ,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffSurnames parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, null ,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffNif parameter is empty")

      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, validStaff.staffSurnames,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress,"",
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffNif parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, validStaff.staffSurnames,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, null,
          validStaff.staffBirth, validStaff.staffFederation.fedId)
      }

      When("staffBirth parameter is null")
      
      intercept[IllegalArgumentException]{
        var insertedStaff: Staff = staffService.createStaff(
          validStaff.staffName, validStaff.staffSurnames,
          validStaff.staffEmail, validStaff.staffTelephones, 
          validStaff.staffAddress, validStaff.staffNif,
          null, validStaff.staffFederation.fedId)
      }
      
      Then("The Staff cannot be created")
      
    }
    
  }
  
  feature("Staff activation") {
    scenario("Staff activation can be changed when Staff exists and parameters are valid") {
      Given("A staff and a new activation state")
      var staff: Staff = new Staff
      var newState: Boolean = true

      //Service to be tested
      var staffService: StaffServiceImpl = new StaffServiceImpl

      staffService.federationService = mock[FederationServiceImpl]
      staffService.staffDao = mock[StaffDao]

      When("Federation exists")
      When("Staff exists")
      When("Staff activation state is valid")
      
      Mockito.when(staffService.staffDao.findById(anyLong)) thenReturn staff

      staffService.changeActivation(anyLong, newState)

      Then("Staff activation has been changed")
      Then("Staff activation has the new state")
      
      assert(staff.staffActivated == newState)
      verify(staffService staffDao) save(staff)

    }
    
    scenario("Staff activation cannot be changed when Staff doesn't exist") {
      Given("A staff, an old activation state and a new activation state")
      var staff: Staff = new Staff
      var oldState: Boolean = false
      var newState: Boolean = true

      //Service to be tested
      var staffService: StaffServiceImpl = new StaffServiceImpl

      staffService.federationService = mock[FederationServiceImpl]
      staffService.staffDao = mock[StaffDao]

      When("Federation exists")
      When("Staff doesn't exist")
      When("Staff activation state is valid")
      
      Mockito.when(staffService.staffDao.findById(anyLong)) thenReturn null

      intercept[InstanceNotFoundException] {
        staffService.changeActivation(anyLong, newState)
      }
      
      Then("Staff activation hasn't been changed")

      assert(staff.staffActivated == oldState)
      verify(staffService staffDao, never) save(staff)
    }
  }
}


