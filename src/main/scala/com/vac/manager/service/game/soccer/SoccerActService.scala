package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.model.game.Game
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.management.InstanceNotFoundException
import java.util.Calendar
import com.vac.manager.model.game.SoccerClassificationEntry
import com.vac.manager.model.competition.LeagueSeasonPK

trait SoccerActService {

  def find(actId: Long): Option[SoccerAct]

  def findGameAct(gameId: Long): Option[SoccerAct]

  def findLeagueSoccerActs(leagueSeason: LeagueSeason): Seq[SoccerAct]

  @throws[DuplicateInstanceException]("If soccer act exists before")
  def createSoccerAct(game: Game): SoccerAct

  @throws[InstanceNotFoundException]("If soccer act doesn't exist")
  def removeSoccerAct(gameId: Long)

  def findSoccerClassificationEntry(teamId: Long, leagueSeasonId: LeagueSeasonPK): SoccerClassificationEntry

  @throws[InstanceNotFoundException]("If local, visitor team or act doesn't exist")
  def editSoccerAct(actId: Long, date: Calendar, location: String, referees: String,
    localTeamId: Long, localResult: Int, visitorTeamId: Long, visitorResult: Int,
    incidents: String, signatures: String): SoccerAct

  @throws[InstanceNotFoundException]("If team or act doesn't exist")
  def editRestSoccerAct(gameId: Long, teamId: Long): SoccerAct

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def setRestState(gameId: Long): SoccerAct

  @throws[InstanceNotFoundException]("If act doesn't exist")
  def unSetRestState(gameId: Long): SoccerAct

}

