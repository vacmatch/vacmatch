package com.vac.manager.service.game.soccer

import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.model.game.Game
import javax.persistence.Entity
import javax.persistence.Table

trait SoccerActService {

  def findGameAct(gameId: Long): Option[SoccerAct]

  def findLeagueSoccerActs(leagueSeason: LeagueSeason): Seq[SoccerAct]

  def createSoccerAct(game: Game): SoccerAct

}

