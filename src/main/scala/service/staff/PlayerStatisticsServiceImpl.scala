package main.scala.service.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import main.scala.model.staff.PlayerStatistics

@Service("playerStatisticsService")
@Transactional
class PlayerStatisticsServiceImpl extends PlayerStatisticsService {
  
  def findById(statsId: Long): PlayerStatistics = {
    null
  }
  
}


