package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableSoccerStaffStats(stats: SoccerStaffStats, slug: String, year: String)
  extends SoccerStaffStats(stats.act, stats.staffMember)
  with UrlGrabber {

  statsId = stats.statsId
  act = stats.act
  staffMember = stats.staffMember
  firstYellowCard = stats.firstYellowCard
  secondYellowCard = stats.secondYellowCard
  redCard = stats.redCard
  // TODO Add goals
  // goals = stats.goals

}

