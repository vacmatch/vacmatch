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
import org.mockito.Mockito._
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import org.scalatest.Matchers._
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.model.personal.Address
import java.util.Calendar

class TeamServiceImplTest
  extends PropSpec
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
  }

  property("teams can be created for any unregistered id") {
    forAll(
      (genValidTeamId, "teamId"),
      (genNonEmptyString, "teamName"),
      (genNonEmptyString, "publicName")) {
        (teamId: Long, teamName: String, publicName: String) =>
          when(teamService.teamDao.findById(teamId)).thenReturn(None)

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

}

