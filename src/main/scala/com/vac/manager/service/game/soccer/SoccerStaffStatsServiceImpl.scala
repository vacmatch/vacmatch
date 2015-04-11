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

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def createLocalStats(actId: Long) = {
    soccerActService.find(actId).map {
      act =>
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
        teamService.findCurrentStaffMemberListByTeam(act.visitorTeam.teamId).map {
          staffMember =>
            val stats = new SoccerStaffStats(act, staffMember)
            soccerStatsDao.save(stats)
        }
    }.getOrElse(throw new InstanceNotFoundException("Act not found"))
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

}

