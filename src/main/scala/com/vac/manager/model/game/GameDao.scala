package com.vac.manager.model.game

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.competition.CompetitionSeasonPK

trait GameDao extends GenericDao[Game, java.lang.Long] {

  def findAllBySeason(competitionSeasonId: CompetitionSeasonPK): Seq[Game]

}