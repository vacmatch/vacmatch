package com.vac.manager.controllers

import com.vac.manager.controllers.utils.Hyperlink
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ Competition, CompetitionSeason }
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.util.Try
import com.vac.manager.controllers.actionable.CompetitionWrapper
import com.vac.manager.controllers.actionable.ActionableCompetitionSeason
import javax.servlet.http.HttpServletRequest

@Controller
class CompetitionSeasonController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var federation: FederationBean = _

  def listCompetitions(
    _model: java.util.Map[String, Object],
    request: HttpServletRequest
  ): String = {
    val model = _model.asScala
    val fedId = federation.getId

    // Check user permissions
    val userCanEdit: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    // Authenticated actions on menu
    val authenticatedActionsMenu: Map[String, String] = Map(
      "Create competition" -> getUrl("CompetitionAdminController.create")
    )

    val actionsMenu: Map[String, String] = if (userCanEdit) authenticatedActionsMenu else Map()

    val competitions = competitionService.findAllByFederation(fedId)
      .map {
        competition =>
          val seasons: Seq[ActionableCompetitionSeason] = competitionService.findSeasonsByCompetitionAsSeq(federation.getId, competition.slug)
            .map(new ActionableCompetitionSeason(_))
          new CompetitionWrapper(competition, seasons, userCanEdit)
      }

    model += "competitions" -> competitions.asJavaCollection
    model += "actionsMenu" -> actionsMenu.asJava.entrySet
    "competition/season/listcompetitions"
  }

  def listSeasons(@PathVariable("slug") slug: String) = {
    val fedId = federation.getId()

    competitionService.findSeasonsByCompetition(fedId, slug).map {
      competition =>

        val seasons: java.util.Collection[ActionableCompetitionSeason] =
          competition.getSeasonList.asScala.map(new ActionableCompetitionSeason(_)).asJava

        new ModelAndView("competition/season/list")
          .addObject("competition", competition)
          .addObject("listLink", getUrl("CompetitionSeasonController.listCompetitions"))
          .addObject("seasons", seasons)

    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def showSeason(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ) = {
    val fedId = federation.getId()

    competitionService.findSeasonByCompetitionSlug(fedId, slug, year).map {
      season =>
        new ModelAndView("competition/season/show")
          .addObject("season", season)
          .addObject("teams", new java.util.ArrayList[AnyRef])
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }
  }

}
