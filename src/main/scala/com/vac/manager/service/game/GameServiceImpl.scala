package com.vac.manager.service.game

import org.springframework.stereotype.Service
import com.vac.manager.model.game.GameDao
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.Game
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.model.competition.CompetitionSeason
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.game.soccer.SoccerActService
import com.vac.manager.model.game.SoccerClassificationEntry
import com.vac.manager.service.team.TeamService

@Service("gameService")
@Transactional
class GameServiceImpl extends GameService {

  @Autowired
  var gameDao: GameDao = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var teamService: TeamService = _

  def find(gameId: Long): Option[Game] = {
    gameDao.findById(gameId)
  }

  def findCompetitionCalendar(competitionSeason: CompetitionSeason): Seq[Game] = {
    gameDao.findAllBySeason(competitionSeason.id)
  }

  def createCalendar[A](teamsNumber: Int, creator: ((Int, A) => Seq[Game]), args: A): Seq[Game] = {
    val games: Seq[Game] = creator(teamsNumber, args)

    games.map { game =>
      // TODO Create act depending on the sport
      gameDao.save(game)
      soccerActService.createSoccerAct(game)
      game
    }
  }

  def createCompetitionCalendar(competitionSeason: CompetitionSeason, teamsNumber: Int, competitionRounds: Int): Seq[Game] = {
    createCalendar(teamsNumber, competitionCalendarCreator, (competitionSeason, competitionRounds))
  }

  @throws[IllegalArgumentException]
  @throws[DuplicateInstanceException]
  @throws[InstanceNotFoundException]
  def competitionCalendarCreator(teamsNumber: Int, args: (CompetitionSeason, Int)): Seq[Game] = {
    val (competitionSeason, competitionRounds) = args
    if ((Option(competitionSeason).nonEmpty) && (teamsNumber > 0) && (competitionRounds > 0)) {

      if (competitionService.findSeasonByCompetitionSlug(
        competitionSeason.id.competition.fedId,
        competitionSeason.id.competition.slug,
        competitionSeason.id.seasonSlug
      ).isEmpty)
        throw new InstanceNotFoundException(competitionSeason.id, "CompetitionSeason")

      if (findCompetitionCalendar(competitionSeason).nonEmpty)
        throw new DuplicateInstanceException(competitionSeason.id, "CompetitionCalendar")

      // Calculate the total number of games
      var gamesNumber: Int = ((competitionRounds * teamsNumber) - competitionRounds) * teamsNumber / 2
      // Gets the number of games that form a gameDay
      var gameDaySize: Int = teamsNumber./(2)

      // When the game's number is odd, this adds some places for the team who rest every gameDay
      if ((teamsNumber.%(2) != 0)) {
        gamesNumber += teamsNumber * competitionRounds
        gameDaySize += 1
      }

      // Calculate the number of game days in a season
      val gameDaysNumber: Int = gamesNumber / gameDaySize

      (1 to gamesNumber)
        .map(game => (game - 1) / gameDaySize)
        .map(gameDay => new Game(competitionSeason, gameDay + 1))
    } else {
      if (Option(competitionSeason).isEmpty)
        throw new IllegalArgumentException(competitionSeason, "CompetitionSeason")
      if (teamsNumber <= 0)
        throw new IllegalArgumentException(teamsNumber, "TeamsNumber")
      //if (competitionRounds <= 0)
      throw new IllegalArgumentException(competitionRounds, "CompetitionRounds")

    }
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def removeCompetitionCalendarFromSeason(competitionSeason: CompetitionSeason) = {
    Option(competitionSeason).map { ls =>
      gameDao.findAllBySeason(ls.id).map {
        game =>
          {
            // TODO Remove act depending on the sport
            soccerActService.removeSoccerAct(game.gameId)
            gameDao.remove(game)
          }
      }
    }.getOrElse(throw new IllegalArgumentException(competitionSeason, "CompetitionSeason"))
  }

  def getCompetitionClassification(competitionSeason: CompetitionSeason): Seq[SoccerClassificationEntry] = {
    // TODO Get act depending on the sport
    teamService.findTeamsByCompetitionSeasonId(competitionSeason.id).map {
      team =>
        val entry: SoccerClassificationEntry =
          soccerActService.findSoccerClassificationEntry(team.teamId, competitionSeason.id)
        entry.team = team
        entry
    }.sortBy(_.assessment).reverse
  }

}

