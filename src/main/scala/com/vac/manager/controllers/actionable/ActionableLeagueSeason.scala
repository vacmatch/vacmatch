package com.vac.manager.controllers.actionable

import scala.beans.BeanProperty
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableLeagueSeason(base: LeagueSeason) extends LeagueSeason with UrlGrabber {
  id = base.id
  startTime = base.startTime
  endTime = base.endTime
  val slug = id.league.slug
  val fedId = id.league.fedId

  @BeanProperty
  val year = id.seasonSlug

  def getLink() = {
    getUrl("LeagueSeasonController.showSeason", "slug" -> slug, "year" -> year)
  }
}
