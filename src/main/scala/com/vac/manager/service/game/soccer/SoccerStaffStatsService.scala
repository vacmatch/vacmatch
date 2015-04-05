package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerStaffStats
import javax.persistence.Entity
import javax.persistence.Table

trait SoccerStaffStatsService {

  def findLocalStats(actId: Long): Seq[SoccerStaffStats]

  def findVisitorStats(actId: Long): Seq[SoccerStaffStats]

}

