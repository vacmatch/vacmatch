package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableSoccerStaffStats(stats: SoccerStaffStats, slug: String, year: String, gameId: java.lang.Long)
  extends SoccerStaffStats(stats.act, stats.staffMember)
  with UrlGrabber {

  statsId = stats.statsId
  act = stats.act
  staffMember = stats.staffMember
  isCalledUp = stats.isCalledUp
  firstYellowCard = stats.firstYellowCard
  secondYellowCard = stats.secondYellowCard
  redCard = stats.redCard
  // TODO Add goals
  // goals = stats.goals

  def getCallUpLink(): String =
    getUrl("GameAdminController.callUpPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

  def getUnCallUpLink(): String =
    getUrl("GameAdminController.unCallUpPost", "slug" -> slug, "year" -> year,
      "gameId" -> gameId, "statsId" -> statsId)

}

