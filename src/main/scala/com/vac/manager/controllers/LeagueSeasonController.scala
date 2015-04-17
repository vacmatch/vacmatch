package com.vac.manager.controllers

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ League, LeagueSeason }
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.FederationBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
class LeagueSeasonController extends UrlGrabber {

  class ActionableLeagueSeason(base: LeagueSeason) extends LeagueSeason with UrlGrabber {
    id = base.id
    val slug = id.league.slug
    val fedId = id.league.fedId

    @BeanProperty
    val year = id.seasonSlug

    def getLink() = {
      getUrl("LeagueSeasonController.showSeason", "slug" -> slug, "year" -> year)
    }
  }

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  def listSeasons(@PathVariable("slug") slug: String) = {
    val fedId = federation.getId()

    val league = leagueService.findSeasonsByLeague(fedId, slug)
    var seasons = null: java.util.Collection[ActionableLeagueSeason]

    val mav = league match {
      case None => new ModelAndView("league/season/notfound")
      case Some(league) => new ModelAndView("league/season/list")
    }

    if (league.nonEmpty)
      seasons = league.get.getSeasonList.asScala.map(new ActionableLeagueSeason(_)).asJava

    mav
      .addObject("league", league.orNull)
      .addObject("listLink", getUrl("LeagueController.list", "fedId" -> fedId))
      .addObject("seasons", seasons)
  }

  def showSeason(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String) = {
    val fedId = federation.getId()

    val season = leagueService.findSeasonByLeagueSlug(fedId, slug, year)

    val mav: ModelAndView = new ModelAndView("league/season/show")
    mav
      .addObject("season", season.get)
      .addObject("teams", new java.util.ArrayList[AnyRef])
  }

}
