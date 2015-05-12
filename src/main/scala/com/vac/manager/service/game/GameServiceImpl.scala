package com.vac.manager.service.game

import org.springframework.stereotype.Service
import com.vac.manager.model.game.GameDao
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.Game
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.model.competition.LeagueSeason
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.management.InstanceNotFoundException
import com.vac.manager.service.game.soccer.SoccerActService
import com.vac.manager.model.game.SoccerClassificationEntry
import com.vac.manager.service.team.TeamService

@Service("gameService")
@Transactional
class GameServiceImpl extends GameService {

  @Autowired
  var gameDao: GameDao = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var teamService: TeamService = _

  def find(gameId: Long): Option[Game] = {
    gameDao.findById(gameId)
  }

  def findLeagueCalendar(leagueSeason: LeagueSeason): Seq[Game] = {
    gameDao.findAllBySeason(leagueSeason.id)
  }

  def createCalendar[A](teamsNumber: Int, creator: ((Int, A) => Seq[Game]), args: A): Seq[Game] = {
    val games: Seq[Game] = creator(teamsNumber, args)
    if (games.isEmpty)
      throw new RuntimeException("No games were created")

    games.map { game =>
      // TODO Create act depending on the sport
      gameDao.save(game)
      soccerActService.createSoccerAct(game)
      game
    }
  }

  def createLeagueCalendar(leagueSeason: LeagueSeason, teamsNumber: Int, leagueRounds: Int): Seq[Game] = {
    createCalendar(teamsNumber, leagueCalendarCreator, (leagueSeason, leagueRounds))
  }

  @throws[IllegalArgumentException]
  @throws[DuplicateInstanceException]
  @throws[InstanceNotFoundException]
  def leagueCalendarCreator(teamsNumber: Int, args: (LeagueSeason, Int)): Seq[Game] = {
    val (leagueSeason, leagueRounds) = args
    if ((Option(leagueSeason).nonEmpty) && (teamsNumber > 0) && (leagueRounds > 0)) {

      if (leagueService.findSeasonByLeagueSlug(
        leagueSeason.id.league.fedId,
        leagueSeason.id.league.slug,
        leagueSeason.id.seasonSlug).isEmpty)
        throw new InstanceNotFoundException("League season not found")

      if (findLeagueCalendar(leagueSeason).nonEmpty)
        throw new DuplicateInstanceException("Existent calendar for this league season")

      // Calculate the total number of games
      var gamesNumber: Int = ((leagueRounds * teamsNumber) - leagueRounds) * teamsNumber / 2
      // Gets the number of games that form a gameDay
      var gameDaySize: Int = teamsNumber./(2)

      // When the game's number is odd, this adds some places for the team who rest every gameDay
      if ((teamsNumber.%(2) != 0)) {
        gamesNumber += teamsNumber * leagueRounds
        gameDaySize += 1
      }

      // Calculate the number of game days in a season
      val gameDaysNumber: Int = gamesNumber / gameDaySize

      (1 to gamesNumber)
        .map(game => (game - 1) / gameDaySize)
        .map(gameDay => new Game(leagueSeason, gameDay + 1))
    } else {
      throw new IllegalArgumentException()
    }
  }

  @throws[IllegalArgumentException]
  def removeLeagueCalendarFromSeason(leagueSeason: LeagueSeason) = {
    Option(leagueSeason).map { ls =>
      gameDao.findAllBySeason(ls.id).map {
        game =>
          {
            // TODO Remove act depending on the sport
            soccerActService.removeSoccerAct(game.gameId)
            gameDao.remove(game)
          }
      }
    }.getOrElse(throw new IllegalArgumentException("Invalid league season parameter"))
  }

  def getLeagueClassification(leagueSeason: LeagueSeason): Seq[SoccerClassificationEntry] = {
    // TODO Get act depending on the sport
    teamService.findTeamsByLeagueSeasonId(leagueSeason.id).map {
      team =>
        val entry: SoccerClassificationEntry =
          soccerActService.findSoccerClassificationEntry(team.teamId, leagueSeason.id)
        entry.team = team
        entry
    }.sortBy(_.assessment).reverse
  }

}

