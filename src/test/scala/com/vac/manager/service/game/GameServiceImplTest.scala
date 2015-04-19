package com.vac.manager.service.game

import org.scalatest.PropSpec
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.BeforeAndAfter
import org.scalatest.GivenWhenThen
import com.vac.manager.model.competition.LeagueSeason
import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import com.vac.manager.model.game.Game
import com.vac.manager.model.game.GameDao
import org.mockito.Mockito
import org.mockito.Mockito._
import org.mockito.Matchers._
import com.vac.manager.service.competition.LeagueService
import org.scalatest.Matchers._
import com.vac.manager.model.competition.LeagueSeasonPK
import com.vac.manager.model.competition.League
import javax.management.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException

class GameServiceImplTest
  extends PropSpec
  with GivenWhenThen
  with MockitoSugar
  with BeforeAndAfter
  with GeneratorDrivenPropertyChecks {

  val genPositiveInts = Gen.choose(1, 50) //arbitrary[Int] suchThat (positiveInt(_))
  val genNegativeOrZeroInts = Gen.choose(-50, 0) //arbitrary[Int] suchThat (negativeInt(_))
  val genValidTeamsNumber = genPositiveInts
  val genValidLeagueRounds = genPositiveInts

  var gameService: GameServiceImpl = _
  var validLeagueSeason: LeagueSeason = _
  var validCalendar: Seq[Game] = _

  before {
    gameService = new GameServiceImpl
    validLeagueSeason = new LeagueSeason
    validLeagueSeason.id = new LeagueSeasonPK
    validLeagueSeason.id.league = new League
    validLeagueSeason.id.league.leagueId = 1
    validLeagueSeason.id.league.slug = "domain.com"
    validLeagueSeason.id.league.fedId = 1
    validLeagueSeason.id.seasonSlug = "senior"
    validCalendar = List(new Game)
  }

  // Create League calendar for a season
  property("A league calendar must be created if parameters are valid") {

    val leagueSeason: LeagueSeason = validLeagueSeason
    forAll(
      (genValidTeamsNumber, "teamsNumber"),
      (genValidLeagueRounds, "leagueRounds")) { (teamsNumber: Int, leagueRounds: Int) =>

        gameService.gameDao = mock[GameDao]
        gameService.leagueService = mock[LeagueService]

        Mockito.when(gameService.leagueService.findSeasonByLeagueSlug(
          leagueSeason.id.league.fedId,
          leagueSeason.id.league.slug,
          leagueSeason.id.seasonSlug)).thenReturn(Some(leagueSeason))

        Mockito.when(gameService.gameDao.findAllBySeason(leagueSeason.id)).thenReturn(List())

        var games: Seq[Game] = gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)

        // Total number of games that should be
        var totalGamesNumber: Int = 0
        if ((teamsNumber.%(2) == 0))
          totalGamesNumber = ((leagueRounds * teamsNumber) - leagueRounds) * teamsNumber / 2
        else
          totalGamesNumber = (((leagueRounds * teamsNumber) - leagueRounds) * teamsNumber / 2) + (teamsNumber * leagueRounds)

        games.size should equal(totalGamesNumber)

      }
  }

  property("A league calendar can't be created if leagueSeason is null") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("Positive teamsNumber and leagueRounds")
    val teamsNumber: Int = 1
    val leagueRounds: Int = 1

    Given("A null leagueSeason")
    val leagueSeason: LeagueSeason = null

    When("Try to create new calendar")
    intercept[IllegalArgumentException] {
      gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)
    }

    Then("An illegal argument exception must be thrown")

  }

  property("A league calendar can't be created if teamsNumber <= 0") {

    val leagueSeason: LeagueSeason = validLeagueSeason
    forAll(
      (genNegativeOrZeroInts, "teamsNumber"),
      (genValidLeagueRounds, "leagueRounds")) {
        (teamsNumber: Int, leagueRounds: Int) =>

          gameService.gameDao = mock[GameDao]
          gameService.leagueService = mock[LeagueService]

          intercept[IllegalArgumentException] {
            gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)
          }
      }
  }

  property("A league calendar can't be created if leagueRounds <= 0") {

    val leagueSeason: LeagueSeason = validLeagueSeason
    forAll(
      (genValidTeamsNumber, "teamsNumber"),
      (genNegativeOrZeroInts, "leagueRounds")) {
        (teamsNumber: Int, leagueRounds: Int) =>

          gameService.gameDao = mock[GameDao]
          gameService.leagueService = mock[LeagueService]

          intercept[IllegalArgumentException] {
            gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)
          }
      }
  }

  property("A league calendar can't be created if leagueSeason doesn't exist") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("Positive teamsNumber and leagueRounds")
    val teamsNumber: Int = 1
    val leagueRounds: Int = 1

    Given("A not existing leagueSeason")
    val leagueSeason: LeagueSeason = validLeagueSeason
    Mockito.when(gameService.leagueService.findSeasonByLeagueSlug(
      leagueSeason.id.league.fedId,
      leagueSeason.id.league.slug,
      leagueSeason.id.seasonSlug)).thenReturn(None)

    When("Try to create a new calendar")
    intercept[InstanceNotFoundException] {
      gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)
    }

    Then("An illegal argument exception must be thrown")

  }

  property("A league calendar can't be created if exists another calendar for this leagueSeason") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("Positive teamsNumber and leagueRounds")
    val teamsNumber: Int = 1
    val leagueRounds: Int = 1

    Given("A existing leagueSeason")
    val leagueSeason: LeagueSeason = validLeagueSeason
    Mockito.when(gameService.leagueService.findSeasonByLeagueSlug(
      leagueSeason.id.league.fedId,
      leagueSeason.id.league.slug,
      leagueSeason.id.seasonSlug)).thenReturn(Some(leagueSeason))

    Given("A existing calendar")
    val calendar: Seq[Game] = validCalendar
    Mockito.when(gameService.findLeagueCalendar(leagueSeason)).thenReturn(calendar)

    When("Try to create a new calendar")
    intercept[DuplicateInstanceException] {
      gameService.createLeagueCalendar(leagueSeason, teamsNumber, leagueRounds)
    }

    Then("A duplicate instance exception must be thrown")

  }

  // Delete League Calendar for a season
  property("A league calendar must be deleted if parameters are valid") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("A existing leagueSeason")
    val leagueSeason: LeagueSeason = validLeagueSeason

    Given("A existing calendar")
    val calendar: Seq[Game] = validCalendar
    Mockito.when(gameService.gameDao.findAllBySeason(leagueSeason.id)).thenReturn(calendar)

    When("Try to remove the calendar")
    gameService.removeLeagueCalendarFromSeason(leagueSeason)

    Then("All games in calendar must be removed")
    calendar.map(game => verify(gameService.gameDao).remove(game))

  }

  property("A league calendar can't be deleted if leagueSeason is null") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("A null leagueSeason")
    val leagueSeason: LeagueSeason = null

    When("Try to remove the calendar")
    intercept[IllegalArgumentException] {
      gameService.removeLeagueCalendarFromSeason(leagueSeason)
    }

    Then("An illegal argument exception must be thrown")

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

  property("A league calendar can't be deleted if leagueSeason doesn't exist") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("A non existing leagueSeason")
    val leagueSeason: LeagueSeason = validLeagueSeason

    Given("A empty calendar for non existing leagueSeason")
    Mockito.when(gameService.gameDao.findAllBySeason(leagueSeason.id)).thenReturn(List())

    When("Try to remove the calendar")
    gameService.removeLeagueCalendarFromSeason(leagueSeason)

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

  property("A league calendar can't be deleted if doesn't exist a created leagueSeason calendar") {

    gameService.gameDao = mock[GameDao]
    gameService.leagueService = mock[LeagueService]

    Given("A existing leagueSeason")
    val leagueSeason: LeagueSeason = validLeagueSeason

    Given("A non existing calendar for this leagueSeason")
    Mockito.when(gameService.gameDao.findAllBySeason(leagueSeason.id)).thenReturn(List())

    When("Try to remove the calendar")
    gameService.removeLeagueCalendarFromSeason(leagueSeason)

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

}

