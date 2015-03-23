package com.vac.manager.controllers.actionable

import com.vac.manager.model.team.Team
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableTeam(team: Team, slug: String, year: String)
  extends Team
  with UrlGrabber {

  teamId = team.teamId
  teamName = team.teamName
  teamActivated = team.teamActivated
  publicTeamName = team.publicTeamName
  foundationDate = team.foundationDate
  teamAddress = team.teamAddress
  teamWeb = team.teamWeb
  teamTelephones = team.teamTelephones
  sponsorsList = team.sponsorsList

  def getEnrollPostLink(): String = {
    getUrl("LeagueSeasonAdminController.enrollTeamInSeasonPost", "teamId" -> team.teamId, "slug" -> slug, "year" -> year)
  }

  def getShowLink(): String = getUrl("TeamController.showTeam", "teamId" -> teamId)

  def getEditLink(): String = "#"

  def getRemoveLink(): String = "#"

}
