package com.vac.manager.controllers.actionable

import scala.beans.BeanProperty
import com.vac.manager.model.competition.CompetitionSeason
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableCompetitionSeason(base: CompetitionSeason) extends CompetitionSeason with UrlGrabber {
  id = base.id
  startTime = base.startTime
  endTime = base.endTime
  val slug = id.competition.slug
  val fedId = id.competition.fedId

  @BeanProperty
  val year = id.seasonSlug

  def getLink() = {
    getUrl("CompetitionSeasonController.showSeason", "slug" -> slug, "year" -> year)
  }
}
