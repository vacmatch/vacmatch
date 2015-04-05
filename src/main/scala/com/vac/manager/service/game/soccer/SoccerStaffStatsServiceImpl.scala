package com.vac.manager.service.game

import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.model.game.soccer.SoccerStaffStatsDao
import javax.transaction.Transactional
import org.springframework.stereotype.Service
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.service.game.soccer.SoccerStaffStatsService

@Service("soccerStaffStatsService")
@Transactional
class SoccerStaffStatsServiceImpl extends SoccerStaffStatsService {

  @Autowired
  var soccerStatsDao: SoccerStaffStatsDao = _

  def findLocalStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findLocalStatsByActId(actId)
  }

  def findVisitorStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findVisitorStatsByActId(actId)
  }

}

