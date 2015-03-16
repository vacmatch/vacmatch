package com.vac.manager.service.game

import org.springframework.stereotype.Service
import com.vac.manager.model.game.GameDao
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.Game
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.model.competition.LeagueSeason
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException

@Service("gameService")
@Transactional
class GameServiceImpl extends GameService {

  @Autowired
  var gameDao: GameDao = _

  @Autowired
  var leagueService: LeagueService = _

  def find(gameId: Long): Option[Game] = {
    gameDao.findById(gameId)
  }

  def findLeagueCalendar(leagueSeason: LeagueSeason): Seq[Game] = {
    gameDao.findAllBySeason(leagueSeason.id)
  }

  @throws[IllegalArgumentException]
  @throws[DuplicateInstanceException]
  def createLeagueCalendar(leagueSeason: LeagueSeason, teamsNumber: Int, leagueRounds: Int): Seq[Game] = {
    if ((Option(leagueSeason).nonEmpty) || (teamsNumber > 0) || (leagueRounds > 0)) {

      if (findLeagueCalendar(leagueSeason).nonEmpty)
        throw new DuplicateInstanceException

      // Calculate the total number of games
      var gamesNumber: Int = ((leagueRounds * teamsNumber) - leagueRounds) * teamsNumber / 2
      // Gets the number of games that form a gameDay
      var gameDaySize: Int = teamsNumber./(2)

      // When the game's number is odd, this adds some places for the team who rest every gameDay
      if ((teamsNumber.%(2) != 0)) {
        gamesNumber += teamsNumber
        gameDaySize += 1
      }

      // Calculate the number of game days in a season
      val gameDaysNumber: Int = gamesNumber / gameDaySize

      var gameDay: Int = 1
      var countDaySize: Int = 0

      for (gameNumber <- 1 to gamesNumber) {
        // Calculate each game day from its size
        if (countDaySize == gameDaySize) {
          countDaySize = 0
          gameDay += 1
        }
        countDaySize += 1

        // Create each new game in her game day
        val game: Game = new Game(leagueSeason, gameDay)
        gameDao.save(game)
      }
      // Returns all inserted games
      findLeagueCalendar(leagueSeason)
    } else {
      throw new IllegalArgumentException()
    }
  }

  def removeLeagueCalendarFromSeason(leagueSeason: LeagueSeason) = {
    gameDao.findAllBySeason(leagueSeason.id).map(game => gameDao.remove(game))
  }

}

