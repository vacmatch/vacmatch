package com.vac.manager.model.game.soccer

import com.vac.manager.model.generic.GenericDao
import javax.persistence.Entity
import javax.persistence.Table

trait SoccerStaffStatsDao extends GenericDao[SoccerStaffStats, java.lang.Long] {

  def findLocalStatsByActId(actId: Long): Seq[SoccerStaffStats]

  def findVisitorStatsByActId(actId: Long): Seq[SoccerStaffStats]

}

