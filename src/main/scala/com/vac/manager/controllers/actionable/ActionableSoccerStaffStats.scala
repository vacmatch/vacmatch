package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.controllers.utils.UrlGrabber
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import com.vac.manager.controllers.utils.Hyperlink

class ActionableSoccerStaffStats(stats: SoccerStaffStats, slug: String, year: String, gameId: java.lang.Long, userCanEdit: Boolean)
  extends SoccerStaffStats(stats.act, stats.staffMember)
  with UrlGrabber {

  statsId = stats.statsId
  act = stats.act
  staffMember = stats.staffMember
  isStaff = stats.isStaff
  staffPosition = stats.staffPosition
  isCalledUp = stats.isCalledUp
  firstYellowCard = stats.firstYellowCard
  secondYellowCard = stats.secondYellowCard
  redCard = stats.redCard
  goals = stats.goals

  def getCallerLink(): Hyperlink =
    if (isCalledUp)
      Hyperlink("Uncall", getUnCallUpLink(), "btn-default")
    else
      Hyperlink("Call", getCallUpLink(), "btn-default")

  def getSetterLink(): Hyperlink =
    if (isStaff)
      Hyperlink("Unset staff", getUnSetStaffLink(), "btn-default")
    else
      Hyperlink("Set staff", getSetStaffLink(), "btn-default")

  def getEditLink(): String = getUrl("GameAdminController.editStatsPost", "slug" -> slug, "year" -> year,
    "gameId" -> gameId, "statsId" -> statsId)

  def getCallUpLink(): String =
    getUrl("GameAdminController.callUpPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

  def getUnCallUpLink(): String =
    getUrl("GameAdminController.unCallUpPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

  def getSetStaffLink(): String =
    getUrl("GameAdminController.setStaffPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

  def getUnSetStaffLink(): String =
    getUrl("GameAdminController.unSetStaffPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

}

