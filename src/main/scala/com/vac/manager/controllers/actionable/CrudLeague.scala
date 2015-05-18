package com.vac.manager.controllers.actionable

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League

class CrudLeague(base: League) extends League with UrlGrabber {

  fedId = base.fedId
  leagueName = base.leagueName
  slug = base.slug

  def getUserLink() = {
    getUrl("LeagueSeasonController.listSeasons", "slug" -> slug)
  }

  def getEditLink() = {
    getUrl("LeagueAdminController.edit", "slug" -> slug)
  }

  def getDeleteLink() = {
    getUrl("LeagueAdminController.delete", "slug" -> slug)
  }

  def getSeasonAdminLink() = {
    getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
  }
}
