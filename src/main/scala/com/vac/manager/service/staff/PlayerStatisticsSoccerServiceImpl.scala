package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.staff.PlayerStatisticsSoccer

@Service("playerStatisticsSoccerService")
@Transactional
class PlayerStatisticsSoccerServiceImpl extends PlayerStatisticsSoccerService {

  override
  def findById(statsId: Long): PlayerStatisticsSoccer = {
    null
  }
  
  def modifyStatistics(statsId: Long, convener: Long, opener: Long, surrogate: Long,
      goals: Long, yellowCards: Long, redCards: Long): PlayerStatisticsSoccer = {
    null
  }
      
}