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
import com.vac.manager.model.staff.Person
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.service.staff.PersonService
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.staff.StaffMemberDao
import javax.management.InstanceNotFoundException

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
    "699555412")

  val genCalendar = for {
    date <- arbDate.arbitrary
  } yield {
    val c: Calendar = Calendar.getInstance()
    c.setTime(date)
    c
  }

  val validAddress = new Address
  val validPerson = new Person
  val validStaffMember = new StaffMember

  before {
    validTeam.teamId = null
    teamService = new TeamServiceImpl
    teamService.teamDao = mock[TeamDao]
    teamService.addressService = mock[AddressService]
    teamService.competitionService = mock[CompetitionService]
    teamService.personService = mock[PersonService]
    teamService.staffMemberDao = mock[StaffMemberDao]
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
            validTeam.teamTelephones)

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
        validTeam.teamTelephones)
    }

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        null,
        validTeam.publicTeamName,
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones)
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
        validTeam.teamTelephones)
    }

    intercept[IllegalArgumentException] {
      teamService.createTeam(
        validTeam.teamName,
        null,
        validTeam.foundationDate,
        validTeam.teamAddress,
        validTeam.teamWeb,
        validTeam.teamTelephones)
    }

    verifyZeroInteractions(teamService.teamDao)

  }

  // Assign staff
  property("A person can be assigned to a team") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(Some(person))

    Given("A non existing relationship between Person and Team")
    val staffMember: StaffMember = validStaffMember
    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId))
      .thenReturn(None)

    When("Try to assign a person to a team")
    val assignedStaffMember: StaffMember = teamService.assignPerson(team.teamId, person.personId)

    Then("Staff modifications must be saved")
    verify(teamService.staffMemberDao).save(assignedStaffMember)

  }

  property("A person can't be assigned to a team if person was assigned before to this team") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("A existing person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(Some(person))

    Given("A relationship between Person and Team")
    val staffMember: StaffMember = validStaffMember
    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId))
      .thenReturn(Some(staffMember))

    When("Try to assign a person to a team")
    intercept[DuplicateInstanceException] {
      teamService.assignPerson(team.teamId, person.personId)
    }

    Then("Must return a duplicate instance exception")

  }

  property("A person can't be assigned to a team if team doesn't exist") {

    Given("A non existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(None)

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(Some(person))

    When("Try to assign a person to a team")
    intercept[InstanceNotFoundException] {
      teamService.assignPerson(team.teamId, person.personId)
    }

    Then("Must return a not found exception")

  }

  property("A person can't be assigned to a team if person doesn't exist") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("A non existing person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(None)

    When("Try to assign a person to a team")
    intercept[InstanceNotFoundException] {
      teamService.assignPerson(team.teamId, person.personId)
    }

    Then("Must return an instance not found exception")

  }

  // Unassign staffMember
  property("A staffMember can be unassigned from a team if person and team exist and it's assigned to this team") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1

    Given("An existing staffMember for this person and team")
    val staffMember: StaffMember = validStaffMember
    staffMember.staffMemberId = 1

    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId)).thenReturn(Some(staffMember))

    When("Try to unassign a staffMember from a team")
    val assignedStaffMember: StaffMember = teamService.unAssignStaff(team.teamId, person.personId)

    Then("StaffMember exit date must be set")
    assert(Option(assignedStaffMember.getExitDate).nonEmpty)

    Then("StaffMember modifications must be saved")
    verify(teamService.staffMemberDao).save(assignedStaffMember)

  }

  property("A staffMember can't be unassigned from a team if team doesn't exist") {

    Given("A non existing team")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1

    Given("A non existing staff member for this non existing team")
    val staffMember: StaffMember = validStaffMember
    staffMember.staffMemberId = 1

    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId)).thenReturn(None)

    When("Try to unassign a staffMember from a team")
    var assignedStaffMember: StaffMember = null
    intercept[InstanceNotFoundException] {
      assignedStaffMember = teamService.unAssignStaff(team.teamId, person.personId)
    }

    Then("Must return a not found exception")

    Then("StaffMember modifications must be saved")
    verify(teamService.staffMemberDao, never).save(assignedStaffMember)

  }

  property("A staffMember can't be unassigned from a team if this staffMember doesn't exist") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1

    Given("An non existing staffMember for this person and team")
    val staffMember: StaffMember = validStaffMember
    staffMember.staffMemberId = 1

    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId)).thenReturn(None)

    When("Try to unassign a staffMember to a team")
    var assignedStaffMember: StaffMember = null
    intercept[InstanceNotFoundException] {
      assignedStaffMember = teamService.unAssignStaff(team.teamId, person.personId)
    }

    Then("Must return a not found exception")

    Then("StaffMember modifications must be saved")
    verify(teamService.staffMemberDao, never).save(assignedStaffMember)

  }

  property("A staffMember can't be unassigned from a team if it exists but it wasn't assigned previously to this team") {

    Given("An existing team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existing person")
    val person: Person = validPerson
    person.personId = 1

    Given("An existing staffMember but not assigned to this team")
    val staffMember: StaffMember = validStaffMember
    staffMember.staffMemberId = 1

    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId)).thenReturn(None)

    When("Try to unassign a staffMember to a team")
    var assignedStaffMember: StaffMember = null
    intercept[InstanceNotFoundException] {
      assignedStaffMember = teamService.unAssignStaff(team.teamId, person.personId)
    }

    Then("Must return a not found exception")

    Then("StaffMember modifications must be saved")
    verify(teamService.staffMemberDao, never).save(assignedStaffMember)

  }

  // Edit Team

  property("Team can be modified if it exists") {

    var existingTeam = validTeam
    existingTeam.teamId = 1

    forAll(
      (genValidTeamId, "teamId"),
      (genNonEmptyString, "teamName"),
      (genNonEmptyString, "publicName"),
      (genCalendar, "foundationDate"),
      (genNonEmptyString, "teamWeb"),
      (genNonEmptyString, "teamTelephones")) {
        (teamId: Long, teamName: String, publicName: String, foundationDate: Calendar,
        teamWeb: String, teamTelephones: String) =>

          val expectedTeam = new Team(
            teamName,
            publicName,
            foundationDate,
            validAddress,
            teamWeb, teamTelephones)
          expectedTeam.teamId = 1

          Mockito.when(teamService.teamDao.findById(teamId)).thenReturn(Some(existingTeam))

          val createdTeam: Team = teamService.modifyTeam(
            teamId,
            teamName,
            publicName,
            foundationDate,
            validAddress,
            teamWeb,
            teamTelephones)

          createdTeam should equal(expectedTeam)

      }
  }

  property("Team can't be modified it doesn't exist") {

    Given("A valid Team's parameters to be modified")
    val team: Team = validTeam
    team.teamId = 1

    Given("A not existing Team")
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(None)

    When("Try to modify a Team")
    intercept[InstanceNotFoundException] {
      teamService.modifyTeam(
        team.teamId,
        team.teamName,
        team.publicTeamName,
        team.foundationDate,
        team.teamAddress,
        team.teamWeb,
        team.teamTelephones)
    }

    Then("Must return an instance not found exception")

  }

  property("Team can't be modified with empty teamName") {

    Given("A valid Team's parameters to be modified")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existing Team")
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(Some(team))

    When("Try to modify a Team")
    intercept[IllegalArgumentException] {
      teamService.modifyTeam(
        team.teamId,
        "",
        team.publicTeamName,
        team.foundationDate,
        team.teamAddress,
        team.teamWeb,
        team.teamTelephones)
    }

    Then("Must return an illegal argument exception")

  }

  property("Team can't be modified with null teamName") {

    Given("A valid Team's parameters to be modified")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existing Team")
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(Some(team))

    When("Try to modify a Team")
    intercept[IllegalArgumentException] {
      teamService.modifyTeam(
        team.teamId,
        null,
        team.publicTeamName,
        team.foundationDate,
        team.teamAddress,
        team.teamWeb,
        team.teamTelephones)
    }

    Then("Must return an illegal argument exception")

  }

  property("Team can't be modified with empty pblicTeamName") {

    Given("A valid Team's parameters to be modified")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existing Team")
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(Some(team))

    When("Try to modify a Team")
    intercept[IllegalArgumentException] {
      teamService.modifyTeam(
        team.teamId,
        team.teamName,
        "",
        team.foundationDate,
        team.teamAddress,
        team.teamWeb,
        team.teamTelephones)
    }

    Then("Must return an illegal argument exception")

  }

  property("Team can't be modified with null publicTeamName") {

    Given("A valid Team's parameters to be modified")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existing Team")
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(Some(team))

    When("Try to modify a Team")
    intercept[IllegalArgumentException] {
      teamService.modifyTeam(
        team.teamId,
        team.teamName,
        null,
        team.foundationDate,
        team.teamAddress,
        team.teamWeb,
        team.teamTelephones)
    }

    Then("Must return an illegal argument exception")

  }

  // Delete Team
  property("Team must be deleted if it exists") {

    Given("An existing Team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(Some(team))

    When("Try to delete a Team")
    teamService.removeTeam(team.teamId)

    Then("Must remove team")
    verify(teamService.teamDao).remove(team)

  }

  property("Team can't be deleted if it doesn't exist") {

    Given("A not existing Team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.teamDao.findById(team.teamId)).thenReturn(None)

    When("Try to delete a Team")
    intercept[InstanceNotFoundException] {
      teamService.removeTeam(team.teamId)
    }

    Then("Must throw an instance not found exception")

  }

}
