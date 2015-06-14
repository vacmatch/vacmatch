package com.vac.manager.model.game.soccer

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.competition.CompetitionSeasonPK
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.game.SoccerClassificationEntry

trait SoccerActDao extends GenericDao[SoccerAct, java.lang.Long] {

  def findByGameId(gameId: Long): Option[SoccerAct]

  def findAllBySeason(competitionSeasonId: CompetitionSeasonPK): Seq[SoccerAct]

  def getLocalEntry(teamId: Long, competitionSeasonId: CompetitionSeasonPK): SoccerClassificationEntry

  def getVisitorEntry(teamId: Long, competitionSeasonId: CompetitionSeasonPK): SoccerClassificationEntry

}