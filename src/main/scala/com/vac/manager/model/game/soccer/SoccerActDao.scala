package com.vac.manager.model.game.soccer

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.competition.LeagueSeasonPK
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Table

trait SoccerActDao extends GenericDao[SoccerAct, java.lang.Long] {

  def findByGameId(gameId: Long): Option[SoccerAct]

  def findAllBySeason(leagueSeasonId: LeagueSeasonPK): Seq[SoccerAct]

}