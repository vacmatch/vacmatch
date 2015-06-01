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
    request: HttpServletRequest
  ): String = {
    val model = _model.asScala
    val fedId = federation.getId

    // Check user permissions
    val userCanEdit: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    // Authenticated actions on menu
    val authenticatedActionsMenu: Map[String, String] = Map(
      "Create league" -> getUrl("LeagueAdminController.create")
    )

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

    leagueService.findSeasonsByLeague(fedId, slug).map {
      league =>

        val seasons: java.util.Collection[ActionableLeagueSeason] =
          league.getSeasonList.asScala.map(new ActionableLeagueSeason(_)).asJava

        new ModelAndView("league/season/list")
          .addObject("league", league)
          .addObject("listLink", getUrl("LeagueSeasonController.listLeagues"))
          .addObject("seasons", seasons)

    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def showSeason(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {
    val fedId = federation.getId()

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        new ModelAndView("league/season/show")
          .addObject("season", season)
          .addObject("teams", new java.util.ArrayList[AnyRef])
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

}
