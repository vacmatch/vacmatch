package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerStaffStats
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team

trait SoccerStaffStatsService {

  def find(statsId: Long): Option[SoccerStaffStats]

  def findLocalStats(actId: Long): Seq[SoccerStaffStats]

  def findVisitorStats(actId: Long): Seq[SoccerStaffStats]

  def findLocalPlayersStats(actId: Long): Seq[SoccerStaffStats]

  def findVisitorPlayersStats(actId: Long): Seq[SoccerStaffStats]

  def findLocalStaffStats(actId: Long): Seq[SoccerStaffStats]

  def findVisitorStaffStats(actId: Long): Seq[SoccerStaffStats]

  def createLocalStats(actId: Long)

  def createVisitorStats(actId: Long)

  def removeLocalStats(actId: Long)

  def removeVisitorStats(actId: Long)

  def callUpStaff(statsId: Long): SoccerStaffStats

  def unCallUpStaff(statsId: Long): SoccerStaffStats

  def setStaff(statsId: Long, position: String): SoccerStaffStats

  def unSetStaff(statsId: Long): SoccerStaffStats

}

