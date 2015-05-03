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

class LeagueServiceImplTest
    extends FeatureSpec
    with GivenWhenThen
    with MockitoSugar
    with Matchers
    with BeforeAndAfter {

  // Generic variables
  var leagueService: LeagueServiceImpl = _
  var validTeam: Team = _
  var validCompetitionMember: CompetitionMember = _
  var validLeagueSeason: LeagueSeason = _

  before {
    leagueService = new LeagueServiceImpl
    leagueService.teamService = mock[TeamService]
    leagueService.leagueSeasonDao = mock[LeagueSeasonDao]
    leagueService.competitionMemberDao = mock[CompetitionMemberDao]

    validTeam = new Team
    validCompetitionMember = new CompetitionMember
    validLeagueSeason = new LeagueSeason
    validLeagueSeason.id = new LeagueSeasonPK
    validLeagueSeason.id.seasonSlug = "2014/2015"
    validLeagueSeason.id.league = new League
    validLeagueSeason.id.league.fedId = 1
    validLeagueSeason.id.league.slug = "Alevines"
  }

  feature("League creation") {
    scenario("Leagues can be created on a federation without leagues") {
      Given("A federation")
      val fedId = 1

      val service = new LeagueServiceImpl
      service.leagueDao = mock[LeagueDao]
      service.leagueSeasonDao = mock[LeagueSeasonDao]

      When("A league for that federation is created with name and slug")

      Then("The league gets created")
      Then("The league has the correct name")
      Then("The league has the correct url")
      forAll("name", "slug") { (name: String, slug: String) =>
        Mockito.when(service.leagueDao.findBySlug(fedId, slug)).thenReturn(None)

        val l = service.createLeague(fedId, name, slug)

        l.leagueName == name &&
          l.slug == slug &&
          service.findBySlug(fedId, slug).isDefined == true
      }
      Then("The url exists")
    }

    scenario("The same league cannot be created twice in the same federation") {
      Given("A federation")
      val fedId = 1

      val service = new LeagueServiceImpl
      service.leagueDao = mock[LeagueDao]
      service.leagueSeasonDao = mock[LeagueSeasonDao]

      When("A league is created for that federation")
      Then("It cannot be created again")

      forAll("name", "slug") { (name: String, slug: String) =>
        Mockito.when(service.leagueDao.findBySlug(fedId, slug)).thenReturn(None)

        val l = service.createLeague(fedId, name, slug)

        Mockito.when(service.leagueDao.findBySlug(fedId, slug)).thenReturn(Some(l))

        intercept[DuplicateInstanceException] {
          service.createLeague(fedId, name, slug)
        }
      }
    }
  }

  feature("Register Team in a League Season") {
    scenario("An existing Team can be registered in a existing League Season ") {

      Given("An existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(Some(leagueSeason))

      Given("A non existing relationship between Team and League Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the League Season")
      val registeredCompMemb: CompetitionMember = leagueService.registerTeamInSeason(leagueSeason.id, team.teamId)

      Then("Team must be registered in this CompetitionMember")
      registeredCompMemb.team should equal(team)

      Then("LeagueSeason must be registered in this CompetitionMember")
      registeredCompMemb.leagueSeason should equal(leagueSeason)

    }

    scenario("A Team can't be registered in a existing League Season if Team doesn't exist") {

      Given("A non existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(None)

      Given("An existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(Some(leagueSeason))

      Given("A non existing relationship between Team and League Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the League Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = leagueService.registerTeamInSeason(leagueSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

    scenario("An existing Team can't be registered in a League Season if League Season doesn't exist") {
      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("A non existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(None)

      Given("A non existing relationship between Team and League Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to register a Team in the League Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = leagueService.registerTeamInSeason(leagueSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

    scenario("An existing Team can't be registered in a existing League Season if it was registered before") {

      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(Some(leagueSeason))

      Given("An existing relationship between Team and League Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(Some(competitionMember))

      When("Try to register a Team in the League Season")
      intercept[DuplicateInstanceException] {
        val registeredCompMemb: CompetitionMember = leagueService.registerTeamInSeason(leagueSeason.id, team.teamId)
      }

      Then("Team can't be registered")

    }

  }

  feature("Remove Team from a League Season") {
    scenario("An existing Competition Member from a Team can be unassigned from a League Season") {

      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(Some(leagueSeason))

      Given("An existing relationship between Team and League Season")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(Some(competitionMember))

      When("Try to remove a Team from a League Season")
      val registeredCompMemb: CompetitionMember = leagueService.removeTeamFromSeason(leagueSeason.id, team.teamId)

      Then("Competition Member endDate must be different to null")
      registeredCompMemb.endDate should not equal (null)

    }

    scenario("An Competition Member can't be unassigned if it doesn't exist") {
      Given("A existing Team")
      val team: Team = validTeam
      team.teamId = 1
      Mockito.when(leagueService.teamService.find(team.teamId)).thenReturn(Some(team))

      Given("An existing League Season")
      val leagueSeason: LeagueSeason = validLeagueSeason
      Mockito.when(leagueService.leagueSeasonDao.findBySlug(
        leagueSeason.id.league.fedId, leagueSeason.id.league.slug, leagueSeason.id.seasonSlug))
        .thenReturn(Some(leagueSeason))

      Given("A non existing CompetitionMember (Relationship between Team and League Season)")
      val competitionMember: CompetitionMember = validCompetitionMember
      Mockito.when(leagueService.competitionMemberDao
        .findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeason.id, team.teamId))
        .thenReturn(None)

      When("Try to remove a Team from a League Season")
      intercept[InstanceNotFoundException] {
        val registeredCompMemb: CompetitionMember = leagueService.removeTeamFromSeason(leagueSeason.id, team.teamId)
      }

      Then("Team can't be removed from a League Season")

    }

  }

}

