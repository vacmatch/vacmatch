package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.staff.PlayerStatistics

@Service("playerStatisticsService")
@Transactional
class PlayerStatisticsServiceImpl extends PlayerStatisticsService {
  
  def findById(statsId: Long): PlayerStatistics = {
    null
  }
  
}


