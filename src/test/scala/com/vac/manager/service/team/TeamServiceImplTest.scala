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
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.staff.StaffMemberDao

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

  var validPerson = new Person
  var validStaffMember = new StaffMember

  // Assign staff
  property("A person can be assigned to a team") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("An existent person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(Some(person))

    Given("A not existent relationship between Person and Team")
    val staffMember: StaffMember = validStaffMember
    Mockito.when(teamService.findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId))
      .thenReturn(None)

    When("Try to assign a person to a team")
    val assignedStaffMember: StaffMember = teamService.assignPerson(team.teamId, person.personId)

    Then("Staff modifications must be saved")
    verify(teamService.staffMemberDao).save(assignedStaffMember)

  }

  property("A person can't be assigned to a team if person was assigned before to this team") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("A existent person")
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

    Given("A not existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(None)

    Given("An existent person")
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

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1
    Mockito.when(teamService.find(team.teamId)).thenReturn(Some(team))

    Given("A not existent person")
    val person: Person = validPerson
    person.personId = 1
    Mockito.when(teamService.personService.find(person.personId)).thenReturn(None)

    When("Try to assign a person to a team")
    intercept[InstanceNotFoundException] {
      teamService.assignPerson(team.teamId, person.personId)
    }

    Then("Must return an instance not found exception")

  }

}
