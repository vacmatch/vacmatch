package com.vac.manager.service.staff

import com.vac.manager.model.staff.PlayerStatistics

trait PlayerStatisticsService {

  def findById(statsId: Long): PlayerStatistics

}