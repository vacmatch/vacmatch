package com.vac.manager.service.game

import org.scalatest.PropSpec
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.BeforeAndAfter
import org.scalatest.GivenWhenThen
import com.vac.manager.model.competition.CompetitionSeason
import org.scalacheck.Gen
import org.scalacheck.Arbitrary._
import com.vac.manager.model.game.Game
import com.vac.manager.model.game.GameDao
import org.mockito.Mockito
import org.mockito.Mockito._
import org.mockito.Matchers._
import com.vac.manager.service.competition.CompetitionService
import org.scalatest.Matchers._
import com.vac.manager.model.competition.CompetitionSeasonPK
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.service.game.soccer.SoccerActService

class GameServiceImplTest
    extends PropSpec
    with GivenWhenThen
    with MockitoSugar
    with BeforeAndAfter
    with GeneratorDrivenPropertyChecks {

  val genPositiveInts = Gen.choose(1, 50) //arbitrary[Int] suchThat (positiveInt(_))
  val genNegativeOrZeroInts = Gen.choose(-50, 0) //arbitrary[Int] suchThat (negativeInt(_))
  val genValidTeamsNumber = genPositiveInts
  val genValidCompetitionRounds = genPositiveInts

  var gameService: GameServiceImpl = _
  var validCompetitionSeason: CompetitionSeason = _
  var validCalendar: Seq[Game] = _
  var validGame: Game = _

  before {
    gameService = new GameServiceImpl
    validCompetitionSeason = new CompetitionSeason
    validCompetitionSeason.id = new CompetitionSeasonPK
    validCompetitionSeason.id.competition = new Competition
    validCompetitionSeason.id.competition.competitionId = 1
    validCompetitionSeason.id.competition.slug = "domain.com"
    validCompetitionSeason.id.competition.fedId = 1
    validCompetitionSeason.id.seasonSlug = "senior"
    validGame = new Game
    validGame.gameId = 1
    validCalendar = List(validGame)
  }

  // Create Competition calendar for a season
  property("A competition calendar must be created if parameters are valid") {

    val competitionSeason: CompetitionSeason = validCompetitionSeason
    forAll(
      (genValidTeamsNumber, "teamsNumber"),
      (genValidCompetitionRounds, "competitionRounds")
    ) { (teamsNumber: Int, competitionRounds: Int) =>

        gameService.gameDao = mock[GameDao]
        gameService.competitionService = mock[CompetitionService]
        gameService.soccerActService = mock[SoccerActService]

        Mockito.when(gameService.competitionService.findSeasonByCompetitionSlug(
          competitionSeason.id.competition.fedId,
          competitionSeason.id.competition.slug,
          competitionSeason.id.seasonSlug
        )).thenReturn(Some(competitionSeason))

        Mockito.when(gameService.gameDao.findAllBySeason(competitionSeason.id)).thenReturn(List())

        var games: Seq[Game] = gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)

        // Total number of games that should be
        var totalGamesNumber: Int = 0
        if ((teamsNumber.%(2) == 0))
          totalGamesNumber = ((competitionRounds * teamsNumber) - competitionRounds) * teamsNumber / 2
        else
          totalGamesNumber = (((competitionRounds * teamsNumber) - competitionRounds) * teamsNumber / 2) + (teamsNumber * competitionRounds)

        games.size should equal(totalGamesNumber)

      }
  }

  property("A competition calendar can't be created if competitionSeason is null") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("Positive teamsNumber and competitionRounds")
    val teamsNumber: Int = 1
    val competitionRounds: Int = 1

    Given("A null competitionSeason")
    val competitionSeason: CompetitionSeason = null

    When("Try to create new calendar")
    intercept[IllegalArgumentException] {
      gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
    }

    Then("An illegal argument exception must be thrown")

  }

  property("A competition calendar can't be created if teamsNumber <= 0") {

    val competitionSeason: CompetitionSeason = validCompetitionSeason
    forAll(
      (genNegativeOrZeroInts, "teamsNumber"),
      (genValidCompetitionRounds, "competitionRounds")
    ) {
        (teamsNumber: Int, competitionRounds: Int) =>

          gameService.gameDao = mock[GameDao]
          gameService.competitionService = mock[CompetitionService]
          gameService.soccerActService = mock[SoccerActService]

          intercept[IllegalArgumentException] {
            gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
          }
      }
  }

  property("A competition calendar can't be created if competitionRounds <= 0") {

    val competitionSeason: CompetitionSeason = validCompetitionSeason
    forAll(
      (genValidTeamsNumber, "teamsNumber"),
      (genNegativeOrZeroInts, "competitionRounds")
    ) {
        (teamsNumber: Int, competitionRounds: Int) =>

          gameService.gameDao = mock[GameDao]
          gameService.competitionService = mock[CompetitionService]
          gameService.soccerActService = mock[SoccerActService]

          intercept[IllegalArgumentException] {
            gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
          }
      }
  }

  property("A competition calendar can't be created if competitionSeason doesn't exist") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("Positive teamsNumber and competitionRounds")
    val teamsNumber: Int = 1
    val competitionRounds: Int = 1

    Given("A not existing competitionSeason")
    val competitionSeason: CompetitionSeason = validCompetitionSeason
    Mockito.when(gameService.competitionService.findSeasonByCompetitionSlug(
      competitionSeason.id.competition.fedId,
      competitionSeason.id.competition.slug,
      competitionSeason.id.seasonSlug
    )).thenReturn(None)

    When("Try to create a new calendar")
    intercept[InstanceNotFoundException] {
      gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
    }

    Then("An illegal argument exception must be thrown")

  }

  property("A competition calendar can't be created if exists another calendar for this competitionSeason") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("Positive teamsNumber and competitionRounds")
    val teamsNumber: Int = 1
    val competitionRounds: Int = 1

    Given("A existing competitionSeason")
    val competitionSeason: CompetitionSeason = validCompetitionSeason
    Mockito.when(gameService.competitionService.findSeasonByCompetitionSlug(
      competitionSeason.id.competition.fedId,
      competitionSeason.id.competition.slug,
      competitionSeason.id.seasonSlug
    )).thenReturn(Some(competitionSeason))

    Given("A existing calendar")
    val calendar: Seq[Game] = validCalendar
    Mockito.when(gameService.findCompetitionCalendar(competitionSeason)).thenReturn(calendar)

    When("Try to create a new calendar")
    intercept[DuplicateInstanceException] {
      gameService.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
    }

    Then("A duplicate instance exception must be thrown")

  }

  // Delete Competition Calendar for a season
  property("A competition calendar must be deleted if parameters are valid") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("A existing competitionSeason")
    val competitionSeason: CompetitionSeason = validCompetitionSeason

    Given("A existing calendar")
    val calendar: Seq[Game] = validCalendar
    Mockito.when(gameService.gameDao.findAllBySeason(competitionSeason.id)).thenReturn(calendar)

    When("Try to remove the calendar")
    gameService.removeCompetitionCalendarFromSeason(competitionSeason)

    Then("All games in calendar must be removed")
    calendar.map(game => verify(gameService.gameDao).remove(game))

  }

  property("A competition calendar can't be deleted if competitionSeason is null") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("A null competitionSeason")
    val competitionSeason: CompetitionSeason = null

    When("Try to remove the calendar")
    intercept[IllegalArgumentException] {
      gameService.removeCompetitionCalendarFromSeason(competitionSeason)
    }

    Then("An illegal argument exception must be thrown")

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

  property("A competition calendar can't be deleted if competitionSeason doesn't exist") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("A non existing competitionSeason")
    val competitionSeason: CompetitionSeason = validCompetitionSeason

    Given("A empty calendar for non existing competitionSeason")
    Mockito.when(gameService.gameDao.findAllBySeason(competitionSeason.id)).thenReturn(List())

    When("Try to remove the calendar")
    gameService.removeCompetitionCalendarFromSeason(competitionSeason)

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

  property("A competition calendar can't be deleted if doesn't exist a created competitionSeason calendar") {

    gameService.gameDao = mock[GameDao]
    gameService.competitionService = mock[CompetitionService]
    gameService.soccerActService = mock[SoccerActService]

    Given("A existing competitionSeason")
    val competitionSeason: CompetitionSeason = validCompetitionSeason

    Given("A non existing calendar for this competitionSeason")
    Mockito.when(gameService.gameDao.findAllBySeason(competitionSeason.id)).thenReturn(List())

    When("Try to remove the calendar")
    gameService.removeCompetitionCalendarFromSeason(competitionSeason)

    Then("None element must be removed")
    verify(gameService.gameDao, never).remove(anyObject())

  }

}

