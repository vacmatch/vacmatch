package com.vac.manager.controllers

import com.vac.manager.controllers.utils.Hyperlink
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ League, LeagueSeason }
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.util.Try
import com.vac.manager.controllers.actionable.LeagueWrapper
import com.vac.manager.controllers.actionable.ActionableLeagueSeason
import javax.servlet.http.HttpServletRequest

@Controller
class LeagueSeasonController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  def listLeagues(
    _model: java.util.Map[String, Object],
    request: HttpServletRequest): String = {
    val model = _model.asScala
    val fedId = federation.getId

    // Check user permissions
    val userCanEdit: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    // Authenticated actions on menu
    val authenticatedActionsMenu: Map[String, String] = Map(
      "Create league" -> getUrl("LeagueAdminController.create"))

    val actionsMenu: Map[String, String] = if (userCanEdit) authenticatedActionsMenu else Map()

    val leagues = leagueService.findAllByFederation(fedId)
      .map {
        league =>
          val seasons: Seq[ActionableLeagueSeason] = leagueService.findSeasonsByLeagueAsSeq(federation.getId, league.slug)
            .map(new ActionableLeagueSeason(_))
          new LeagueWrapper(league, seasons, userCanEdit)
      }

    model += "competitions" -> leagues.asJavaCollection
    model += "actionsMenu" -> actionsMenu.asJava.entrySet
    "league/season/listleagues"
  }

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
