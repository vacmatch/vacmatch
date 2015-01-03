package com.vac.manager.controllers

import java.util.Calendar
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League
import com.vac.manager.model.competition.LeagueSeason
import com.vac.manager.service.competition.LeagueService
import org.resthub.web.springmvc.router.Router
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
class LeagueSeasonController() extends UrlGrabber {

  class ActionableLeagueSeason(base: LeagueSeason) extends LeagueSeason with UrlGrabber {
    id = base.id
    val slug = id.league.slug
    val fedId = id.league.fedId
    val year = id.seasonSlug

    def getLink() = {
      getUrl("LeagueSeasonController.showSeason", "slug" -> slug, "year" -> year) + "?fedId=" + fedId
    }

    def getEditLink() = {
      getUrl("LeagueSeasonController.edit", "slug" -> slug, "year" -> year) + "?fedId=" + fedId
    }

    def getDeleteLink() = {
      getUrl("LeagueSeasonController.delete", "slug" -> slug, "year" -> year) + "?fedId=" + fedId
    }
  }

  @Autowired
  var leagueService: LeagueService = _

  def listSeasons(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String) = {

    val league = leagueService.findSeasonsByLeague(fedId, slug)
    var seasons = null: java.util.Collection[LeagueSeason]

    val mav: ModelAndView = league match {
      case None => new ModelAndView("league/notfound")
      case Some(league) => new ModelAndView("league/season/list")
    }

    if (league nonEmpty)
      seasons = league.get.getSeasonList

    mav
      .addObject("league", league.orNull)
      .addObject("seasons", seasons)
  }

  def edit(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {

    val season = leagueService.findSeasonByLeagueSlug(fedId, slug, year)

    val mav: ModelAndView = new ModelAndView("league/season/edit_form")
    mav
      .addObject("season", season.get)
      .addObject("verb", "Edit")
      .addObject("submitUrl", getUrl("LeagueSeasonController.editPost", "slug" -> slug, "year" -> year))
  }

  def delete(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {

    val season = leagueService.findSeasonByLeagueSlug(fedId, slug, year)

    val mav: ModelAndView = new ModelAndView("league/season/edit_form")
    mav
      .addObject("season", season.get)
      .addObject("verb", "Remove")
      .addObject("submitUrl", getUrl("LeagueSeasonController.deletePost", "slug" -> slug, "year" -> year))
  }

  def editPost(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {

  }

  def deletePost(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {

  }

  def showSeason(
    @RequestParam("fedId") fedId: Long,
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {

    val season = leagueService.findSeasonByLeagueSlug(fedId, slug, year)

    val mav: ModelAndView = new ModelAndView("league/season/show")
    mav
      .addObject("season", season.get)
  }

}
