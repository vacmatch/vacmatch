package com.vac.manager.service.game

import com.vac.manager.model.game.Game
import com.vac.manager.model.competition.CompetitionSeason
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.game.ClassificationEntry

trait CompetitionStrategyService {

  /**
    * Create needed matches for a season
    * @param competitionSeason The season
    * @param teamsNumber The number of teams which are going to compete that season
    * @param competitionRounds The number of rounds that this competition will have
    * @throws IllegalArgumentException if teamsNumber or competitionRound are <= 0 or if competitionSeason is null
    * @throws DuplicateInstanceException if exists another calendar created for this season
    * @throws InstanceNotFoundException if competitionSeason doesn't exist
    * @return The full list of matches for the season
    */
  @throws[IllegalArgumentException]
  @throws[InstanceNotFoundException]
  def createCompetitionCalendar(competitionSeason: CompetitionSeason, teamsNumber: Int, competitionRounds: Int): Seq[Game]

  /**
    * Remove all games from a season
    * @throws IllegalArgumentException if competitionSeason is null
    * @throws InstanceNotFoundException if it tries to remove a not existent act
    */
  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def removeCompetitionCalendarFromSeason(competitionSeason: CompetitionSeason)

  @throws[IllegalArgumentException]("If competition season doesn't exist")
  def getCompetitionClassification(competitionSeasson: CompetitionSeason): Seq[ClassificationEntry]

}
