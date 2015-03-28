package com.vac.manager.controllers.actionable

import com.vac.manager.model.competition.CompetitionMember
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableCompetitionMember(cm: CompetitionMember, slug: String, year: String)
  extends CompetitionMember
  with UrlGrabber {

  id = cm.id
  startDate = cm.startDate
  endDate = cm.endDate
  leagueSeason = cm.leagueSeason
  team = cm.team

  def getDeleteEnrollPostLink(): String = {
    "/"
  }

}
