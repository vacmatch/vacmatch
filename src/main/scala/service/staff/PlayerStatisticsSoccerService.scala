package main.scala.service.staff

import main.scala.model.staff.PlayerStatisticsSoccer

trait PlayerStatisticsSoccerService extends PlayerStatisticsService{
  
  def findById(statsId: Long): PlayerStatisticsSoccer
  
  def modifyStatistics(statsId: Long, convener: Long, opener: Long, surrogate: Long,
      goals: Long, yellowCards: Long, redCards: Long): PlayerStatisticsSoccer

}


