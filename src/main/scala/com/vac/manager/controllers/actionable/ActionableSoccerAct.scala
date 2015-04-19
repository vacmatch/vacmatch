package com.vac.manager.controllers.actionable

import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.controllers.utils.UrlGrabber
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import com.vac.manager.controllers.utils.Hyperlink

class ActionableSoccerAct(val act: SoccerAct, val slug: String, val year: String, userCanEdit: Boolean)
  extends SoccerAct
  with UrlGrabber {

  actId = act.actId
  game = act.game
  date = act.date
  location = act.location
  localTeam = act.localTeam
  localResult = act.localResult
  visitorTeam = act.visitorTeam
  visitorResult = act.visitorResult
  referees = act.referees
  incidents = act.incidents
  signatures = act.signatures

  val anonymousLinks = List(Hyperlink("Show game", getShowLink(), "btn-default"))

  @BeanProperty
  val authorizedLinks = if (!userCanEdit) List().asJava else
    List(Hyperlink("Edit game", getEditLink(), "btn-default")).asJava

  @BeanProperty
  val links = (anonymousLinks ++ authorizedLinks.asScala).asJava

  def getShowLink(): String = getUrl("GameController.show", "slug" -> slug, "year" -> year, "gameId" -> game.gameId)

  def getEditLink(): String = getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> game.gameId)

}

