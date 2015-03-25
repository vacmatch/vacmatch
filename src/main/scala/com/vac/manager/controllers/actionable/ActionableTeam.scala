package com.vac.manager.controllers.actionable

import com.vac.manager.model.team.Team
import com.vac.manager.controllers.utils.UrlGrabber
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import com.vac.manager.controllers.utils.Hyperlink

class ActionableTeam(team: Team, slug: String, year: String, userCanEdit: Boolean)
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

  @BeanProperty
  val anonymousLinks = List(Hyperlink("Show team", getShowLink, "btn-primary"),
    Hyperlink("Show staff members", getShowStaffMembersLink, "btn-default")).asJava

  @BeanProperty
  val authorizedLinks = if (!userCanEdit) List().asJava else {
    List(Hyperlink("Edit team", getEditLink, "btn-default"),
      Hyperlink("Assing staff", getAssignStaffLink, "btn-default"),
      Hyperlink("Delete team", getDeleteLink, "btn-default")).asJava
  }

  @BeanProperty
  val links = (anonymousLinks.asScala ++ authorizedLinks.asScala).asJava

  def getShowLink(): String = getUrl("TeamController.showTeam", "teamId" -> teamId)

  def getEditLink(): String = getUrl("TeamAdminController.edit", "teamId" -> teamId)

  def getAssignStaffLink(): String = getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId)

  def getShowStaffMembersLink(): String = getUrl("TeamController.showStaffMembers", "teamId" -> teamId)

  def getDeleteLink(): String = getUrl("TeamAdminController.delete", "teamId" -> teamId)

}
