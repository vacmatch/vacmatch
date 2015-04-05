package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.model.game.Game
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import javax.management.InstanceNotFoundException

trait SoccerActService {

  def findGameAct(gameId: Long): Option[SoccerAct]

  def findLeagueSoccerActs(leagueSeason: LeagueSeason): Seq[SoccerAct]

  @throws[DuplicateInstanceException]("If soccer act exists before")
  def createSoccerAct(game: Game): SoccerAct

  @throws[InstanceNotFoundException]("If soccer act doesn't exist")
  def removeSoccerAct(gameId: Long)

}

