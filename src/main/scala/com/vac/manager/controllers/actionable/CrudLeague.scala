package com.vac.manager.controllers.actionable

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.Competition

class CrudCompetition(base: Competition) extends Competition with UrlGrabber {

  fedId = base.fedId
  competitionName = base.competitionName
  slug = base.slug

  def getUserLink() = {
    getUrl("CompetitionSeasonController.listSeasons", "slug" -> slug)
  }

  def getEditLink() = {
    getUrl("CompetitionAdminController.edit", "slug" -> slug)
  }

  def getDeleteLink() = {
    getUrl("CompetitionAdminController.delete", "slug" -> slug)
  }

  def getSeasonAdminLink() = {
    getUrl("CompetitionSeasonAdminController.list", "slug" -> slug)
  }
}
