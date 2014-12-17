package test.scala.model.TeamService

import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.Assert._
import java.util.Calendar
import test.scala.util.GlobalNames.Spring_test_config_file
import main.scala.util.GlobalNames.Spring_config_file
import main.scala.service.team.TeamService
import main.scala.model.team.TeamDao
import main.scala.model.generic.exceptions.NotImplementedException
import main.scala.model.team.Team
import main.scala.model.generic.exceptions.DuplicateInstanceException
import main.scala.model.generic.exceptions.IncorrectDateException
import javax.management.InstanceNotFoundException
import main.scala.model.generic.exceptions.IncorrectNameException
import main.scala.Application

@RunWith(classOf[SpringJUnit4ClassRunner])
//@ContextConfiguration(locations = Array("classpath:/application.xml", "classpath:/spring-config-test.xml"))
@SpringApplicationConfiguration(classes = Array(classOf[Application]), locations = Array("classpath:/spring-config-test.xml"))
//@Transactional
class TeamServiceTest {

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

    //Insert team and get it's id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
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

    //Insert team and get it's id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
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

    //Insert teams and expect exception in second team
    var insertedTeam1: Team = teamService.createTeam(teamName, fundationalDate1)
    var insertedTeam2: Team = teamService.createTeam(teamName, fundationalDate2)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullTeamName = {

    //Init variables
    var teamName: String = null
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert teams and expect exception in team name
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)

   }

  @Test (expected = classOf[IncorrectDateException])
  def createTeamWithIncorrectDate = {

    //Init variables
    var teamName: String = "Test name"

    //Set fundationalDate 1 year in the future
    var fundationalDate: Calendar = Calendar.getInstance()
    fundationalDate.add(Calendar.YEAR, 1)

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def createTeamWithNullDate = {

    //Init variables
    var teamName: String = "Test name"

    //Set fundationalDate
    var fundationalDate: Calendar = null

    //Insert team and expect exception
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)

  }

  /**
   * ------------------ modifyTeamName -------------------
   */
  @Test
  def modifyTeamName {

    //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
    var insertedTeamId: Long = insertedTeam.teamId

    //Modify team name
    var newTeamName: String = "Modified name"
    teamService.modifyTeamName(insertedTeamId, newTeamName)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.teamName, newTeamName)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamNameWithNonExistentTeam {

    //Try to modify non existent team and expect exception
    var teamName: String = "Test name"
    teamService.modifyTeamName(Non_existent_team_id, teamName)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamNameWithNullName {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
    var insertedTeamId: Long = insertedTeam.teamId

    //Modify team name with illegal teamName
    var newTeamName: String = null
    teamService.modifyTeamName(insertedTeamId, newTeamName)

  }


  /**
   * ------------------ modifyTeamDate -------------------
   */
  @Test
  def modifyTeamDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
    var insertedTeamId: Long = insertedTeam.teamId

    //Modify team date
    var newFundationalDate: Calendar = Calendar.getInstance()
    teamService.modifyTeamDate(insertedTeamId, newFundationalDate)

    //Get team and check result
    var modifiedTeam: Team = teamService.findByTeamId(insertedTeamId)
    assertEquals(modifiedTeam.fundationDate, newFundationalDate)

  }

  @Test (expected = classOf[InstanceNotFoundException])
  def modifyTeamDateWithNonExistentTeam {

    //Modify not existent team date and expect exception
    var newFundationalDate: Calendar = Calendar.getInstance()
    teamService.modifyTeamDate(Non_existent_team_id , newFundationalDate)

  }

  @Test (expected = classOf[IncorrectDateException])
  def modifyTeamDateWithIncorrectDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
    var insertedTeamId: Long = insertedTeam.teamId

    //Modify team date 1 year in the future and expect exception
    var newFundationalDate: Calendar = Calendar.getInstance()
    newFundationalDate.add(Calendar.YEAR, 1)
    teamService.modifyTeamDate(insertedTeamId, newFundationalDate)

  }

  @Test (expected = classOf[IllegalArgumentException])
  def modifyTeamDateWithNullDate {

        //Init variables
    var teamName: String = "Test name"
    var fundationalDate: Calendar = Calendar.getInstance()

    //Insert team and get id
    var insertedTeam: Team = teamService.createTeam(teamName, fundationalDate)
    var insertedTeamId: Long = insertedTeam.teamId

    //Modify team date with null and expect exception
    var newFundationalDate: Calendar = null
    teamService.modifyTeamDate(insertedTeamId, newFundationalDate)

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
