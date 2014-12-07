package main.scala.service.staff

import main.scala.model.staff.PlayerStatistics

trait PlayerStatisticsService {

  def findById(statsId: Long): PlayerStatistics

}