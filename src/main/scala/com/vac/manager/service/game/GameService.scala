package com.vac.manager.service.game

import com.vac.manager.model.game.Game
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.management.InstanceNotFoundException

trait GameService {

  def find(gameId: Long): Option[Game]

  def findLeagueCalendar(leagueSeason: LeagueSeason): Seq[Game]

  /**
    * Create needed matches for a season
    * @param leagueSeason The season
    * @param teamsNumber The number of teams which are going to compete that season
    * @param leagueRounds The number of rounds that this league will have
    * @throws IllegalArgumentException if teamsNumber or leagueRound are <= 0 or if leagueSeason is null
    * @throws DuplicateInstanceException if exists another calendar created for this season
    * @throws InstanceNotFoundException if leagueSeason doesn't exist
    * @return The full list of matches for the season
    */
  @throws[IllegalArgumentException]
  @throws[DuplicateInstanceException]
  @throws[InstanceNotFoundException]
  def createLeagueCalendar(leagueSeason: LeagueSeason, teamsNumber: Int, leagueRounds: Int): Seq[Game]

  /**
    * Remove all games from a season
    */
  @throws[IllegalArgumentException]
  def removeLeagueCalendarFromSeason(leagueSeason: LeagueSeason)

}

