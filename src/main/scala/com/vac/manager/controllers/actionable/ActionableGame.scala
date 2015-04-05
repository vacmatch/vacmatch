package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.Game
import com.vac.manager.controllers.utils.UrlGrabber
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import com.vac.manager.controllers.utils.Hyperlink

class ActionableGame(val game: Game, val slug: String, val year: String, userCanEdit: Boolean)
    extends Game
    with UrlGrabber {

  gameId = game.gameId
  leagueSeason = game.leagueSeason
  matchDay = game.matchDay

  val showLink = getUrl("GameController.show", "slug" -> slug, "year" -> year, "gameId" -> game.gameId)
  val editLink = "#" // TODO: getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> game.gameId)

  val anonymousLinks = List(Hyperlink("Show game", showLink, "btn-primary"))

  val authorizedLinks = if (!userCanEdit) List() else
    List(Hyperlink("Edit game", editLink, "btn-default"))

  @BeanProperty
  val links = (anonymousLinks ++ authorizedLinks).asJava

}

