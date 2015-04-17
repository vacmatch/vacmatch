package com.vac.manager.service.competition

import org.springframework.stereotype.Service
import com.vac.manager.model.competition.Competition

@Service("competitionService")
class CompetitionServiceImpl extends CompetitionService {

  def find(compId: Long): Option[Competition] = {
    None
  }

}