package com.vac.manager.model.TeamService

import javax.transaction.Transactional
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.Assert._
import java.util.Calendar
import com.vac.manager.util.test.GlobalNames.Spring_test_config_file
import com.vac.manager.service.team.TeamService
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.generic.exceptions.NotImplementedException
import com.vac.manager.model.team.Team
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.IncorrectDateException
import javax.management.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IncorrectNameException
import com.vac.manager.Application
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement

@RunWith(classOf[SpringJUnit4ClassRunner])
//@ContextConfiguration(locations = Array("classpath:/application.xml", "classpath:/spring-config-test.xml"))
@SpringApplicationConfiguration(classes = Array(classOf[Application]), locations = Array("classpath:/spring-config-test.xml"))
@EnableTransactionManagement
@Transactional
class TeamServiceTest extends JUnitSuite {

  private val Non_existent_team_id: Long = -1


  @Autowired
  private var teamService: TeamService = _

  @Autowired
  private var teamDao: TeamDao = _

  @Autowired
  private var addressService: AddressService = _


  //Default variables

  var defaultTeamName: String = "Test name"
  var defaultTeamPublicName: String = "Test public name"
  var defaultTeamFundationalDate: Calendar = Calendar.getInstance()
  var defaultTeamAddress: Address = addressService.createAddress("USA")
  var defaultTeamWeb: String = "www.teamweb.com"


  /**
   * ------------------ FindTeamsByFederationId ---------------------
   */

  @Test
  def testFindAndGetNumberOfTeamsByFederationId = {

  }

  @Test
  def testFindAndGetNumberOfTeamsByFederationIdIncorrect = {

  }

  @Test
  def testFindAndGetNumberOfTeamsByFederationIdWithCountLimitation = {

  }

  @Test
  def testFindAndGetNumberOfTeamsByFederationIdOrdered = {

  }

  @Test
  def testFindAndGetNumberOfTeamsByFederationIdEmptyList = {

  }

  @Test
  def testFindTeamsByFederationIdWithNonExistentFederation = {

  }

  @Test
  def testGetNumberOfTeamsByFederationIdWithNonExistentFederation = {

  }

  /**
   * ------------------ FindByTeamId -------------------
   */
  @Test
  def testFindByTeamId = {

    //Insert team and get it's id
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)
    var teamId: Long = insertedTeam.teamId

    //Get Team
    var resultTeam: Team = teamService.findByTeamId(teamId)

    //Check data
    assertEquals(insertedTeam,resultTeam)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def testFindByTeamIdNonExistentTeam = {

    //Get Team
    var resultTeam: Team = teamService.findByTeamId(Non_existent_team_id)

  }


  /**
   *  ------------------ createTeam -------------------
   */
  @Test
  def createTeam = {

    //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    var address: String = "Third Avenue"

    //Insert team and get it's id
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)
    var teamId: Long = insertedTeam.teamId

    //Get Team
    var resultTeam: Team = teamService.findByTeamId(teamId)

    //Check data
    assertEquals(insertedTeam,resultTeam)

  }

  @Test (expected = classOf[DuplicateInstanceException])
  def createTeamWithExistentTeamName = {
    //Init variables
    var publicName2: String = "Other test public name"
    var fundationalDate2: Calendar = Calendar.getInstance()
    var address2: Address = addressService.createAddress("USA")
    var web2: String = "www.otherweb.com"

    //Insert teams and expect exception in second team
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)
    var insertedTeam2: Team = teamService.createTeam(defaultTeamName,
        publicName2, fundationalDate2,
        address2, web2)
  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullTeamName = {

    //Init variables
    var nullTeamName: String = null

    var insertedTeam: Team = teamService.createTeam(nullTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

   }

  @Test (expected = classOf[IncorrectDateException])
  def createTeamWithIncorrectDate = {
    //Set fundationalDate 1 year in the future
    var fundationalDate: Calendar = Calendar.getInstance()
    fundationalDate.add(Calendar.YEAR, 1)

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, fundationalDate,
        defaultTeamAddress, defaultTeamWeb)
  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullDate = {
      //Set fundationalDate
    var nullFundationalDate: Calendar = null

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, nullFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

  }

  /**
   * ------------------ modifyTeamName -------------------
   */
  @Test
  def modifyTeamName {
    //Insert team and get non modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamPublicName: String = insertedTeam.publicTeamName
    var insertedTeamDate: Calendar = insertedTeam.fundationDate
    var insertedTeamAddress: Address = insertedTeam.teamAddress
    var insertedTeamWeb: String = insertedTeam.teamWeb

    //Modify team name
    var newTeamName: String = "Modified name"
    teamService.modifyTeam(insertedTeamId, newTeamName, insertedTeamPublicName,
        insertedTeamDate, insertedTeamAddress, insertedTeamWeb)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.teamName, newTeamName)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamNameWithNonExistentTeam {

    //Try to modify non existent team and expect exception
    var teamName: String = "Test name"
    var teamPublicName: String = "Test public name"
    var teamDate: Calendar = Calendar.getInstance()
    var teamAddress: Address = addressService.createAddress("USA")
    var teamWeb: String = "www.teamweb.com"

    teamService.modifyTeam(Non_existent_team_id, defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamNameWithNullName {

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamPublicName: String = insertedTeam.publicTeamName
    var insertedTeamDate: Calendar = insertedTeam.fundationDate
    var insertedTeamAddress: Address = insertedTeam.teamAddress
    var insertedTeamWeb: String = insertedTeam.teamWeb

    //Modify team name with illegal teamName
    var nullTeamName: String = null
    teamService.modifyTeam(insertedTeamId, nullTeamName, insertedTeamPublicName,
        insertedTeamDate, insertedTeamAddress, insertedTeamWeb)

  }


  /**
   * ------------------ modifyTeamDate -------------------
   */
  @Test
  def modifyTeamDate {
    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamPublicName: String = insertedTeam.publicTeamName
    var insertedTeamDate: Calendar = insertedTeam.fundationDate
    var insertedTeamAddress: Address = insertedTeam.teamAddress
    var insertedTeamWeb: String = insertedTeam.teamWeb

    //Modify team date
    var newFundationalDate: Calendar = Calendar.getInstance()
    teamService.modifyTeam(insertedTeamId, insertedTeamName, insertedTeamPublicName,
        newFundationalDate, insertedTeamAddress, insertedTeamWeb)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.fundationDate, newFundationalDate)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamDateWithNonExistentTeam {

    //Modify not existent team date and expect exception
    var newFundationalDate: Calendar = Calendar.getInstance()

    teamService.modifyTeam(Non_existent_team_id, defaultTeamName,
        defaultTeamPublicName, newFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

  }

  @Test (expected = classOf[IncorrectDateException])
  def modifyTeamDateWithIncorrectDate {
    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamPublicName: String = insertedTeam.publicTeamName
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamAddress: Address = insertedTeam.teamAddress
    var insertedTeamWeb: String = insertedTeam.teamWeb

    //Modify team date 1 year in the future and expect exception
    var newFundationalDate: Calendar = Calendar.getInstance()
    newFundationalDate.add(Calendar.YEAR, 1)

    teamService.modifyTeam(insertedTeamId, insertedTeamName,
        insertedTeamPublicName, newFundationalDate,
        insertedTeamAddress, insertedTeamWeb)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamDateWithNullDate {

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamPublicName: String = insertedTeam.publicTeamName
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamAddress: Address = insertedTeam.teamAddress
    var insertedTeamWeb: String = insertedTeam.teamWeb

    //Modify team date with null and expect exception
    var nullFundationalDate: Calendar = null

    teamService.modifyTeam(insertedTeamId, insertedTeamName,
        insertedTeamPublicName, nullFundationalDate,
        insertedTeamAddress, insertedTeamWeb)
  }

  /**
   * ------------------ modifySponsors -----------------
   */
  @Test
  def modifySponsors = {

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    //Modify sponsorList
    var newSponsors: List[String] = List("Pepsi","Lotto")
    teamService.modifyTeamSponsors(insertedTeam.teamId, newSponsors)

    //Get team
    var team: Team = teamService.findByTeamId(insertedTeam.teamId)

    assertEquals(team.sponsorsList, insertedTeam.sponsorsList)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyStaffWithIncorrectSponsorList = {

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(defaultTeamName,
        defaultTeamPublicName, defaultTeamFundationalDate,
        defaultTeamAddress, defaultTeamWeb)

    //Modify sponsorList and expect exception
    var newSponsors: List[String] = List(null,"Lotto")

    teamService.modifyTeamSponsors(insertedTeam.teamId, newSponsors)

  }

  /**
   * ------------------ modifyStaff -------------------
   */
  @Test
  def modifyStaff = {

  }

  @Test
  def modifyStaffWithNonExistentTeam = {

  }

  @Test
  def modifyStaffWithIncorrectStaffList = {

  }

  /**
   * ------------------ modifyCompetitions -------------------
   */
  @Test
  def modifyCompetitions = {

  }

  @Test
  def modifyCompetitionsWithNonExistentTeam = {

  }

  @Test
  def modifyCompetitionsWithIncorrectCompetitionsList = {

  }

}
