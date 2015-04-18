package com.vac.manager.service.competition

import com.vac.manager.model.competition.Competition

trait CompetitionService {

  def find(compId: Long): Option[Competition]

}