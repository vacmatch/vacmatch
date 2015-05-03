package com.vac.manager.service.game

import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.model.game.soccer.SoccerStaffStatsDao
import javax.transaction.Transactional
import org.springframework.stereotype.Service
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.service.game.soccer.SoccerStaffStatsService
import com.vac.manager.service.game.soccer.SoccerActService
import com.vac.manager.service.team.TeamService
import javax.management.InstanceNotFoundException
import com.vac.manager.model.team.Team
import java.util.Calendar
import scala.collection.JavaConverters._

@Service("soccerStaffStatsService")
@Transactional
class SoccerStaffStatsServiceImpl extends SoccerStaffStatsService {

  @Autowired
  var soccerStatsDao: SoccerStaffStatsDao = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var teamService: TeamService = _

  def find(statsId: Long): Option[SoccerStaffStats] = {
    soccerStatsDao.findById(statsId)
  }

  def findLocalStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findLocalStatsByActId(actId)
  }

  def findVisitorStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findVisitorStatsByActId(actId)
  }

  def findLocalPlayersStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findLocalStatsByActId(actId).filter(staff => !staff.isStaff)
  }

  def findVisitorPlayersStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findVisitorStatsByActId(actId).filter(staff => !staff.isStaff)
  }

  def findLocalStaffStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findLocalStatsByActId(actId).filter(staff => staff.isStaff)
  }

  def findVisitorStaffStats(actId: Long): Seq[SoccerStaffStats] = {
    soccerStatsDao.findVisitorStatsByActId(actId).filter(staff => staff.isStaff)
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def createLocalStats(actId: Long) = {
    soccerActService.find(actId).map {
      act =>
        if (Option(act.localTeam).isEmpty)
          throw new InstanceNotFoundException("Local team doesn't exist")

        teamService.findCurrentStaffMemberListByTeam(act.localTeam.teamId).map {
          staffMember =>
            val stats = new SoccerStaffStats(act, staffMember)
            soccerStatsDao.save(stats)
        }
    }.getOrElse(throw new InstanceNotFoundException("Act not found"))
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def createVisitorStats(actId: Long) = {
    soccerActService.find(actId).map {
      act =>
        if (Option(act.visitorTeam).isEmpty)
          throw new InstanceNotFoundException("Visitor team doesn't exist")

        teamService.findCurrentStaffMemberListByTeam(act.visitorTeam.teamId).map {
          staffMember =>
            val stats = new SoccerStaffStats(act, staffMember)
            soccerStatsDao.save(stats)
        }
    }.getOrElse(throw new InstanceNotFoundException("Act not found"))
  }

  @throws[InstanceNotFoundException]("If stats doesn't exist")
  def editStats(statsId: Long, firstYellowCard: Calendar,
    secondYellowCard: Calendar, redCard: Calendar, goals: Seq[Calendar]) = {

    find(statsId).map {
      stats =>
        stats.firstYellowCard = firstYellowCard
        stats.secondYellowCard = secondYellowCard
        stats.redCard = redCard
        stats.goals = goals.asJava
    }.getOrElse(throw new InstanceNotFoundException("Stats not found"))
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def removeLocalStats(actId: Long) = {
    soccerActService.find(actId).map {
      act =>
        findLocalStats(actId).map {
          stats =>
            soccerStatsDao.remove(stats)
        }
    }.getOrElse(throw new InstanceNotFoundException("Act not found"))
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def removeVisitorStats(actId: Long) = {
    soccerActService.find(actId).map {
      act =>
        findVisitorStats(actId).map {
          stats =>
            soccerStatsDao.remove(stats)
        }
    }.getOrElse(throw new InstanceNotFoundException("Act not found"))
  }

  def callUpStaff(statsId: Long): SoccerStaffStats = {
    find(statsId).map {
      stats =>
        stats.isCalledUp = true
        soccerStatsDao.save(stats)
        stats
    }.getOrElse(throw new InstanceNotFoundException("Soccer Staff Stats not found"))
  }

  def unCallUpStaff(statsId: Long): SoccerStaffStats = {
    find(statsId).map {
      stats =>
        stats.isCalledUp = false
        soccerStatsDao.save(stats)
        stats
    }.getOrElse(throw new InstanceNotFoundException("Soccer Staff Stats not found"))
  }

  def setStaff(statsId: Long, position: String): SoccerStaffStats = {
    find(statsId).map {
      stats =>
        stats.isStaff = true
        stats.staffPosition = position
        soccerStatsDao.save(stats)
        stats
    }.getOrElse(throw new InstanceNotFoundException("Soccer Staff Stats not found"))
  }

  def unSetStaff(statsId: Long): SoccerStaffStats = {
    find(statsId).map {
      stats =>
        stats.isStaff = false
        stats.staffPosition = ""

        soccerStatsDao.save(stats)
        stats
    }.getOrElse(throw new InstanceNotFoundException("Soccer Staff Stats not found"))
  }

}

