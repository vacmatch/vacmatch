package com.vac.manager.controllers.actionable

import com.vac.manager.model.competition.CompetitionMember
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableCompetitionMember(cm: CompetitionMember, slug: String, year: String)
    extends CompetitionMember
    with UrlGrabber {

  id = cm.id
  startDate = cm.startDate
  endDate = cm.endDate
  competitionSeason = cm.competitionSeason
  team = cm.team

  def getDisenrollPostLink(): String = {
    getUrl("CompetitionSeasonAdminController.disenrollTeamInSeasonPost", "teamId" -> team.teamId, "slug" -> slug, "year" -> year)
  }

}
