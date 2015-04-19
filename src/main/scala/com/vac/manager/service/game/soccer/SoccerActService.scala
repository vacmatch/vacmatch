package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.model.game.Game
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.management.InstanceNotFoundException
import java.util.Calendar

trait SoccerActService {

  def find(actId: Long): Option[SoccerAct]

  def findGameAct(gameId: Long): Option[SoccerAct]

  def findLeagueSoccerActs(leagueSeason: LeagueSeason): Seq[SoccerAct]

  @throws[DuplicateInstanceException]("If soccer act exists before")
  def createSoccerAct(game: Game): SoccerAct

  @throws[InstanceNotFoundException]("If soccer act doesn't exist")
  def removeSoccerAct(gameId: Long)

  @throws[InstanceNotFoundException]("If local, visitor team or act doesn't exist")
  def editSoccerAct(actId: Long, date: Calendar, location: String, referees: String,
    localTeamId: Long, localResult: Int, visitorTeamId: Long, visitorResult: Int,
    incidents: String, signatures: String): SoccerAct

}

