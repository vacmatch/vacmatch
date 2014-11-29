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

    //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    var address: String = "Third Avenue"

    //Insert team and get it's id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)
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
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)
    var teamId: Long = insertedTeam.teamId

    //Get Team
    var resultTeam: Team = teamService.findByTeamId(teamId)

    //Check data
    assertEquals(insertedTeam,resultTeam)

  }

  @Test (expected = classOf[DuplicateInstanceException])
  def createTeamWithExistentTeamName = {

    //Init variables
    var teamName: String = "Test name"
    var fundationalDate1: Calendar = Calendar.getInstance()
    var fundationalDate2: Calendar = Calendar.getInstance()

    var address1: String = "Third Avenue"
    var address2: String = "Second Avenue"

    //Insert teams and expect exception in second team
    var insertedTeam1: Team = teamService.createTeam(teamName, fundationalDate1, address1)
    var insertedTeam2: Team = teamService.createTeam(teamName, fundationalDate2, address2)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullTeamName = {

    //Init variables
    var teamName: String = null
    var fundationalDate: Calendar = Calendar.getInstance()

    var address: String = "Third Avenue"

    //Insert teams and expect exception in team name
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)

   }

  @Test (expected = classOf[IncorrectDateException])
  def createTeamWithIncorrectDate = {

    //Init variables
    var teamName: String = "Test name"
    var address: String = "Third Avenue"

    //Set fundationalDate 1 year in the future
    var fundationalDate: Calendar = Calendar.getInstance()
    fundationalDate.add(Calendar.YEAR, 1)

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)
  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullDate = {

    //Init variables
    var teamName: String = "Test name"
    var address: String = "Third Avenue"

    //Set fundationalDate
    var fundationalDate: Calendar = null

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)
  }

  /**
   * ------------------ modifyTeam -------------------
   */
  @Test
  def modifyTeamName {

    //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var address: String = "Third Avenue"

    //Insert team and get non modified parameters
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, address)
    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamDate: Calendar = insertedTeam.fundationDate
    var insertedTeamAddress: String = insertedTeam.teamAddress

    //Modify team name
    var newTeamName: String = "Modified name"
    teamService.modifyTeam(insertedTeamId, newTeamName, insertedTeamDate, insertedTeamAddress)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.teamName, newTeamName)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamNameWithNonExistentTeam {

    //Try to modify non existent team and expect exception
    var teamName: String = "Test name"
    var teamDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    teamService.modifyTeam(Non_existent_team_id, teamName, teamDate, teamAddress)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamNameWithNullName {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)
    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamDate: Calendar = insertedTeam.fundationDate
    var insertedTeamAddress: String = insertedTeam.teamAddress

    //Modify team name with illegal teamName
    var newTeamName: String = null
    teamService.modifyTeam(insertedTeamId, newTeamName, insertedTeamDate, insertedTeamAddress)

  }


  /**
   * ------------------ modifyTeam -------------------
   */
  @Test
  def modifyTeamDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)
    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamAddress: String = insertedTeam.teamAddress

    //Modify team date
    var newFundationalDate: Calendar = Calendar.getInstance()
    teamService.modifyTeam(insertedTeamId, insertedTeamName, newFundationalDate, insertedTeamAddress)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.fundationDate, newFundationalDate)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamDateWithNonExistentTeam {

    //Modify not existent team date and expect exception
    var teamName: String = "Test name"
    var teamAddress: String = "Third Avenue"
    var newFundationalDate: Calendar = Calendar.getInstance()
    teamService.modifyTeam(Non_existent_team_id, teamName, newFundationalDate, teamAddress)

  }

  @Test (expected = classOf[IncorrectDateException])
  def modifyTeamDateWithIncorrectDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get not modified parameters
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)
    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamAddress: String = insertedTeam.teamAddress

    //Modify team date 1 year in the future and expect exception
    var newFundationalDate: Calendar = Calendar.getInstance()
    newFundationalDate.add(Calendar.YEAR, 1)
    teamService.modifyTeam(insertedTeamId, insertedTeamName, newFundationalDate, insertedTeamAddress)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamDateWithNullDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)
    var insertedTeamId: Long = insertedTeam.teamId
    var insertedTeamName: String = insertedTeam.teamName
    var insertedTeamAddress: String = insertedTeam.teamAddress

    //Modify team date with null and expect exception
    var newFundationalDate: Calendar = null
    teamService.modifyTeam(insertedTeamId, insertedTeamName, newFundationalDate, insertedTeamAddress)

  }

  /**
   * ------------------ modifySponsors -----------------
   */
  @Test
  def modifySponsors = {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)

    //Modify sponsorList
    var newSponsors: List[String] = List("Pepsi","Lotto")
    teamService.modifyTeamSponsors(insertedTeam.teamId, newSponsors)

    //Get team again
    var team: Team = teamService.findByTeamId(insertedTeam.teamId)

    assertEquals(team.sponsorsList, insertedTeam.sponsorsList)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyStaffWithIncorrectSponsorList = {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()
    var teamAddress: String = "Third Avenue"

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate, teamAddress)

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
