package com.vac.manager.service.competition

import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.mockito.Mockito
import org.mockito.Mockito.{ when => whenMock, _ }
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks._
import com.vac.manager.model.competition._
import com.vac.manager.model.team.Team
import com.vac.manager.service.team.TeamService
import org.scalatest.BeforeAndAfter
import javax.management.InstanceNotFoundException

/*
   This is a stub test class. To learn how to customize it,
see the documentation for `ensime-goto-test-configs'
*/

class CompetitionServiceImplTest
    extends FeatureSpec
    with GivenWhenThen
    with MockitoSugar
    with Matchers
    with BeforeAndAfter {

  // Generic variables
  var competitionService: CompetitionServiceImpl = _
  var validTeam: Team = _
  var validCompetitionMember: CompetitionMember = _
  var validCompetitionSeason: CompetitionSeason = _

  before {
    competitionService = new CompetitionServiceImpl
    competitionService.teamService = mock[TeamService]
    competitionService.competitionSeasonDao = mock[CompetitionSeasonDao]
    competitionService.competitionMemberDao = mock[CompetitionMemberDao]

    validTeam = new Team
    validCompetitionMember = new CompetitionMember
    validCompetitionSeason = new CompetitionSeason
    validCompetitionSeason.id = new CompetitionSeasonPK
    validCompetitionSeason.id.seasonSlug = "2014/2015"
    validCompetitionSeason.id.competition = new Competition
    validCompetitionSeason.id.competition.fedId = 1
    validCompetitionSeason.id.competition.slug = "Alevines"
  }

  feature("Competition creation") {
    scenario("Competitions can be created on a federation without competitions") {
      Given("A federation")
      val fedId = 1

      val service = new CompetitionServiceImpl
      service.competitionDao = mock[CompetitionDao]
      service.competitionSeasonDao = mock[CompetitionSeasonDao]

      When("A competition for that federation is created with name and slug")

      Then("The competition gets created")
      Then("The competition has the correct name")
      Then("The competition has the correct url")
      forAll("name", "slug") { (name: String, slug: String) =>
        Mockito.when(service.competitionDao.findBySlug(fedId, slug)).thenReturn(None)

        val l = service.createCompetition(fedId, name, slug)

        l.competitionName == name &&
          l.slug == slug &&
          service.findBySlug(fedId, slug).isDefined == true
      }
      Then("The url exists")
    }

    scenario("The same competition cannot be created twice in the same federation") {
      Given("A federation")
      val fedId = 1

      val service = new CompetitionServiceImpl
      service.competitionDao = mock[CompetitionDao]
      service.competitionSeasonDao = mock[CompetitionSeasonDao]

      When("A competition is created for that federation")
      Then("It cannot be created again")

      forAll("name", "slug") { (name: String, slug: String) =>
        Mockito.when(service.competitionDao.findBySlug(fedId, slug)).thenReturn(None)

        val l = service.createCompetition(fedId, name, slug)

        Mockito.when(service.competitionDao.findBySlug(fedId, slug)).thenReturn(Some(l))

        intercept[DuplicateInstanceException] {
          service.createCompetition(fedId, name, slug)
        }
      }
    }
  }

  feature("Register Team in a Competition Season") {
    scenario("An existing Team can be registered in a existing Competition Season ") {

      Given("An existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(Some(competitionSeason))

      Given("A non existing relationship between Team and Competition Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the Competition Season")
      val registeredCompMemb: CompetitionMember = competitionService.registerTeamInSeason(competitionSeason.id, team.teamId)

      Then("Team must be registered in this CompetitionMember")
      registeredCompMemb.team should equal(team)

      Then("CompetitionSeason must be registered in this CompetitionMember")
      registeredCompMemb.competitionSeason should equal(competitionSeason)

    }

    scenario("A Team can't be registered in a existing Competition Season if Team doesn't exist") {

      Given("A non existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(None)

      Given("An existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(Some(competitionSeason))

      Given("A non existing relationship between Team and Competition Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the Competition Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = competitionService.registerTeamInSeason(competitionSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

    scenario("An existing Team can't be registered in a Competition Season if Competition Season doesn't exist") {
      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("A non existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(None)

      Given("A non existing relationship between Team and Competition Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the Competition Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = competitionService.registerTeamInSeason(competitionSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

    scenario("An existing Team can't be registered in a existing Competition Season if it was registered before") {

      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(Some(competitionSeason))

      Given("An existing relationship between Team and Competition Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(Some(competitionMember))

      When("Try to register a Team in the Competition Season")
      intercept[DuplicateInstanceException] {
        val registeredCompMemb: CompetitionMember = competitionService.registerTeamInSeason(competitionSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

  }

  feature("Remove Team from a Competition Season") {
    scenario("An existing Competition Member from a Team can be unassigned from a Competition Season") {

      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(Some(competitionSeason))

      Given("An existing relationship between Team and Competition Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(Some(competitionMember))

      When("Try to remove a Team from a Competition Season")
      val registeredCompMemb: CompetitionMember = competitionService.removeTeamFromSeason(competitionSeason.id, team.teamId)

      Then("Competition Member endDate must be different to null")
      registeredCompMemb.endDate should not equal (null)

    }

    scenario("An Competition Member can't be unassigned if it doesn't exist") {
      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(competitionService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing Competition Season")
      val competitionSeason: CompetitionSeason = validCompetitionSeason
      Mockito.when(competitionService.competitionSeasonDao.findBySlug(
        competitionSeason.id.competition.fedId, competitionSeason.id.competition.slug, competitionSeason.id.seasonSlug
      ))
        .thenReturn(Some(competitionSeason))

      Given("A non existing CompetitionMember (Relationship between Team and Competition Season)")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(competitionService.competitionMemberDao
        .findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to remove a Team from a Competition Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = competitionService.removeTeamFromSeason(competitionSeason.id, team.teamId)
      }

      Then("Team can't be removed from a Competition Season")

    }

  }

}

