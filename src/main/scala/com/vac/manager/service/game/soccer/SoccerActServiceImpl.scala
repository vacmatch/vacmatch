package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.soccer.SoccerActDao
import com.vac.manager.model.competition.LeagueSeason
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import com.vac.manager.model.game.Game
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.persistence.Entity
import javax.persistence.Table
import javax.management.InstanceNotFoundException
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.service.team.TeamService

@Service("soccerActService")
@Transactional
class SoccerActServiceImpl extends SoccerActService {

  @Autowired
  var soccerActDao: SoccerActDao = _

  @Autowired
  var teamService: TeamService = _

  def find(actId: Long): Option[SoccerAct] = {
    soccerActDao.findById(actId)
  }

  def findGameAct(gameId: Long): Option[SoccerAct] = {
    soccerActDao.findByGameId(gameId)
  }

  def findLeagueSoccerActs(leagueSeason: LeagueSeason): Seq[SoccerAct] = {
    soccerActDao.findAllBySeason(leagueSeason.id)
  }

  @throws[DuplicateInstanceException]("If soccer act exists before")
  def createSoccerAct(game: Game): SoccerAct = {
    findGameAct(game.gameId).map {
      act =>
        {
          throw new DuplicateInstanceException("Existing soccer act")
        }
    }.getOrElse {
      val act = new SoccerAct(game)
      soccerActDao.save(act)
      act
    }
  }

  @throws[InstanceNotFoundException]("If soccer act doesn't exist")
  def removeSoccerAct(gameId: Long) {
    findGameAct(gameId).map {
      act =>
        {
          soccerActDao.remove(act)
        }
    }.getOrElse(throw new DuplicateInstanceException("Existing soccer act"))
  }

  @throws[InstanceNotFoundException]("If local, visitor team or act doesn't exist")
  def editSoccerAct(actId: Long, date: Calendar, location: String, referees: String,
    localTeamId: Long, visitorTeamId: Long, incidents: String, signatures: String): SoccerAct = {

    find(actId).map {
      act =>
        {
          val localTeam: Team = teamService.find(localTeamId)
            .getOrElse(throw new InstanceNotFoundException("Local Team not found"))
          val visitorTeam: Team = teamService.find(visitorTeamId)
            .getOrElse(throw new InstanceNotFoundException("Visitor Team not found"))

          act.date = date
          act.location = location
          act.referees = referees
          act.localTeam = localTeam
          act.visitorTeam = visitorTeam
          act.incidents = incidents
          act.signatures = signatures
          soccerActDao.save(act)
          act
        }
    }.getOrElse(throw new InstanceNotFoundException("Soccer act not found"))
  }

}

