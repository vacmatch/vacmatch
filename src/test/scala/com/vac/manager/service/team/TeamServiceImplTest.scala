package com.vac.manager.service.team

import org.scalatest.PropSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.mock.MockitoSugar
import org.scalacheck.Arbitrary._
import org.scalatest.BeforeAndAfter
import com.vac.manager.service.personal.AddressService
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.model.team.TeamDao
import org.scalacheck.Gen
import org.mockito.Mockito
import org.mockito.Mockito._
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import org.scalatest.Matchers._
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.model.personal.Address
import java.util.Calendar
import org.scalatest.GivenWhenThen
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.staff.StaffMemberHistoric
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.service.staff.StaffMemberHistoricService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

class TeamServiceImplTest
  extends PropSpec
  with GivenWhenThen
  with MockitoSugar
  with GeneratorDrivenPropertyChecks
  with BeforeAndAfter {

  def nonEmptyString(s: String) = { s != null && s.trim.nonEmpty }
  def validTeamName(s: String) = nonEmptyString(s)
  def validPublicName(s: String) = nonEmptyString(s)

  val genNonEmptyString = arbitrary[String] suchThat (nonEmptyString(_))
  val genValidTeamName = arbitrary[String] suchThat (validTeamName(_))
  val genValidPublicName = arbitrary[String] suchThat (validPublicName(_))
  val genValidTeamId = Gen.choose(1, 1000000000L)
  var teamService: TeamServiceImpl = _
  var validTeam = new Team(
    "Chiringuito FC",
    "Chiringuito Football Club",
    Calendar.getInstance(),
    new Address,
    "wwww.elchiringuitofc.com",
    List("699555412").asJava)

  before {
    validTeam.teamId = null
    teamService = new TeamServiceImpl
    teamService.teamDao = mock[TeamDao]
    teamService.addressService = mock[AddressService]
    teamService.competitionService = mock[CompetitionService]
    teamService.staffService = mock[StaffMemberService]
    teamService.staffHistoricService = mock[StaffMemberHistoricService]
  }

  property("teams can be created for any unregistered id") {
    forAll(
      (genValidTeamId, "teamId"),
      (genNonEmptyString, "teamName"),
      (genNonEmptyString, "publicName")) {
        (teamId: Long, teamName: String, publicName: String) =>
          Mockito.when(teamService.teamDao.findById(teamId)).thenReturn(None)

          val expectedTeam: Team = validTeam
          expectedTeam.teamName = teamName
          expectedTeam.publicTeamName = publicName

          val createdTeam: Team = teamService.createTeam(
            teamName,
            publicName,
            validTeam.foundationDate,
            validTeam.teamAddress,
            validTeam.teamWeb,
            validTeam.teamTelephones.asScala)

          createdTeam should equal(expectedTeam)

      }
  }

  property("teams can't be created with empty or null teamName") {

    assert(Option(validTeam.publicTeamName).exists(_.trim != ""))

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        "",
        validTeam.publicTeamName,
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones.asScala)
    }

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        null,
        validTeam.publicTeamName,
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones.asScala)
    }

    verifyZeroInteractions(teamService.teamDao)

  }

  property("teams can't be created with empty or null publicTeamName") {

    assert(Option(validTeam.teamName).exists(_.trim != ""))

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        validTeam.teamName,
        "",
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones.asScala)
    }

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        validTeam.teamName,
        null,
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones.asScala)
    }

    verifyZeroInteractions(teamService.teamDao)

  }

  var validStaff = new StaffMember
  var validStaffHistoric = new StaffMemberHistoric

  // Assign staff
  property("A staff can be assigned to a team") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existent staff")
    val staff: StaffMember = validStaff
    staff.staffId = 1
    Mockito.when(teamService.staffService.find(staff.staffId)).thenReturn(Some(staff))

    Given("A historic between Staff and Team")
    val staffHistoric: StaffMemberHistoric = validStaffHistoric
    Mockito.when(teamService.staffHistoricService.create(staff, team)).thenReturn(Right(staffHistoric))

    When("Try to assign a staff to a team")
    val eitherTeam: Either[Exception, Team] = teamService.assignStaff(team.teamId, staff.staffId)

    Then("Staff historic must be created")
    verify(teamService.staffHistoricService).create(staff, team)

    Then("Staff modifications must be saved")
    verify(teamService.teamDao).save(team)

    Then("Staff historic must be assigned")
    assert(eitherTeam.right.get.staffHistoricList.contains(staffHistoric))

  }

  property("A staff can't be assigned to a team if team doesn't exist") {

    Given("A not existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(None)

    Given("An existent staff")
    val staff: StaffMember = validStaff
    staff.staffId = 1
    Mockito.when(teamService.staffService.find(staff.staffId)).thenReturn(Some(staff))

    Given("A historic between Staff and Team")
    val staffHistoric: StaffMemberHistoric = validStaffHistoric
    Mockito.when(teamService.staffHistoricService.create(staff, team)).thenReturn(Right(staffHistoric))

    When("Try to assign a staff to a team")
    val eitherTeam: Either[Exception, Team] = teamService.assignStaff(team.teamId, staff.staffId)

    Then("Must return an error")
    assert(eitherTeam.isLeft)

    Then("Must return a not found exception")
    assert(eitherTeam.left.get.isInstanceOf[InstanceNotFoundException])

    Then("Staff historic mustn't be created")
    verify(teamService.staffHistoricService, never).create(staff, team)

  }

  property("A staff can't be assigned to a team if staff doesn't exist") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("A not existent staff")
    val staff: StaffMember = validStaff
    staff.staffId = 1
    Mockito.when(teamService.staffService.find(staff.staffId)).thenReturn(None)

    When("Try to assign a staff to a team")
    val eitherTeam: Either[Exception, Team] = teamService.assignStaff(team.teamId, staff.staffId)

    Then("Must return an error")
    assert(eitherTeam.isLeft)

    Then("Must return an instance not found exception")
    assert(eitherTeam.left.get.isInstanceOf[InstanceNotFoundException])

    Then("Staff historic mustn't be created")
    verify(teamService.staffHistoricService, never).create(staff, team)

  }

}
