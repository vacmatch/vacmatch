package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.Game
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableGame(game: Game, sl: String, ye: String)
  extends Game
  with UrlGrabber {

  var slug: String = sl
  var year: String = ye

  gameId = game.gameId
  leagueSeason = game.leagueSeason
  matchDay = game.matchDay
  act = game.act

  def getShowLink() = getUrl("GameController.show", "slug" -> slug, "year" -> year, "gameId" -> gameId)

}

