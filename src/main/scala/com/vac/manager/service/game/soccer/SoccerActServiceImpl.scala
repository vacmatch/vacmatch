package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.soccer.SoccerActDao
import com.vac.manager.model.competition.CompetitionSeason
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import com.vac.manager.model.game.Game
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.service.team.TeamService
import com.vac.manager.model.competition.CompetitionSeasonPK
import com.vac.manager.model.game.soccer.SoccerClassificationEntry

@Service("soccerActService")
@Transactional
class SoccerActServiceImpl extends SoccerActService {

  @Autowired
  var soccerActDao: SoccerActDao = _

  @Autowired
  var soccerStaffStatsService: SoccerStaffStatsService = _

  @Autowired
  var teamService: TeamService = _

  def find(actId: Long): Option[SoccerAct] = {
    soccerActDao.findById(actId)
  }

  def findGameAct(gameId: Long): Option[SoccerAct] = {
    soccerActDao.findByGameId(gameId)
  }

  def findCompetitionSoccerActs(competitionSeason: CompetitionSeason): Seq[SoccerAct] = {
    soccerActDao.findAllBySeason(competitionSeason.id)
  }

  def findSoccerClassificationEntry(teamId: Long, competitionSeasonId: CompetitionSeasonPK): SoccerClassificationEntry = {
    val local: SoccerClassificationEntry = soccerActDao.getLocalEntry(teamId, competitionSeasonId)
    val visitor: SoccerClassificationEntry = soccerActDao.getVisitorEntry(teamId, competitionSeasonId)

    new SoccerClassificationEntry(
      local.assessment + visitor.assessment,
      local.team,
      local.played + visitor.played,
      local.won + visitor.won,
      local.drawn + visitor.drawn,
      local.lost + visitor.lost,
      local.goalsFor + visitor.goalsFor,
      local.goalsAgainst + visitor.goalsAgainst,
      local.goalDifference + visitor.goalDifference,
      local.points + visitor.points
    )

  }

  @throws[DuplicateInstanceException]("If soccer act exists before")
  def createSoccerAct(game: Game): SoccerAct = {
    findGameAct(game.gameId).map {
      act =>
        {
          throw new DuplicateInstanceException(act, "SoccerAct")
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
    }.getOrElse(throw new InstanceNotFoundException(gameId, "SoccerAct"))
  }

  @throws[InstanceNotFoundException]("If local, visitor team or act doesn't exist")
  def editSoccerAct(actId: Long, date: Calendar, location: String, referees: String,
    localTeamId: Long, localResult: Int, visitorTeamId: Long, visitorResult: Int,
    incidents: String, signatures: String): SoccerAct = {

    find(actId).map {
      act =>
        {
          val localTeam: Team = teamService.find(localTeamId)
            .orNull
          val visitorTeam: Team = teamService.find(visitorTeamId)
            .orNull

          val oldLocal: Team = act.localTeam
          val oldVisitor: Team = act.visitorTeam

          act.date = date
          act.location = location
          act.referees = referees
          act.localTeam = localTeam
          act.localResult = localResult
          act.visitorTeam = visitorTeam
          act.visitorResult = visitorResult
          act.incidents = incidents
          act.signatures = signatures
          soccerActDao.save(act)

          // If any team change, staff stats must be changed
          if (oldLocal != localTeam) {
            soccerStaffStatsService.removeLocalStats(actId)
            if (Option(localTeam).nonEmpty)
              soccerStaffStatsService.createLocalStats(actId)
          }
          if (oldVisitor != visitorTeam) {
            soccerStaffStatsService.removeVisitorStats(actId)
            if (Option(visitorTeam).nonEmpty)
              soccerStaffStatsService.createVisitorStats(actId)
          }

          act
        }
    }.getOrElse(throw new InstanceNotFoundException(actId, "SoccerAct"))
  }

  @throws[InstanceNotFoundException]("If team or act doesn't exist")
  def editRestSoccerAct(gameId: Long, teamId: Long): SoccerAct = {
    findGameAct(gameId).map {
      act =>
        val team: Team = teamService.find(teamId)
          .getOrElse(null)

        act.localTeam = team

        // Reset visitor
        act.localResult = 0
        act.visitorTeam = null
        act.visitorResult = 0
        soccerActDao.save(act)

        // Staff stats must be removed
        soccerStaffStatsService.removeLocalStats(act.actId)
        soccerStaffStatsService.removeVisitorStats(act.actId)

        act
    }.getOrElse(throw new InstanceNotFoundException(gameId, "SoccerAct"))
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def setRestState(gameId: Long): SoccerAct = {
    findGameAct(gameId).map { act =>
      act.isRest = true

      // Staff stats must be removed
      if (Option(act.localTeam).nonEmpty)
        soccerStaffStatsService.removeLocalStats(act.actId)
      if (Option(act.visitorTeam).nonEmpty)
        soccerStaffStatsService.removeVisitorStats(act.actId)

      // Reset visitor
      act.localResult = 0
      act.visitorTeam = null
      act.visitorResult = 0
      soccerActDao.save(act)

      act
    }.getOrElse(throw new InstanceNotFoundException(gameId, "SoccerAct"))
  }

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def unSetRestState(gameId: Long): SoccerAct = {
    findGameAct(gameId).map { act =>
      act.isRest = false

      // Staff stats must be removed
      if (Option(act.localTeam).nonEmpty)
        soccerStaffStatsService.createLocalStats(act.actId)
      if (Option(act.visitorTeam).nonEmpty)
        soccerStaffStatsService.removeVisitorStats(act.actId)

      // Reset visitor
      act.localResult = 0
      act.visitorTeam = null
      act.visitorResult = 0
      soccerActDao.save(act)

      act
    }.getOrElse(throw new InstanceNotFoundException(gameId, "SoccerAct"))
  }

}

