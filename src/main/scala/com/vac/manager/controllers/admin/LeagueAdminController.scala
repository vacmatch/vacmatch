package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.Layout
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import java.lang.Long
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._
import com.vac.manager.controllers.actionable.CrudLeague
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ModelAttribute
import javax.validation.Valid

@Controller
@Layout("layouts/default_admin")
class LeagueAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  def show(
    @RequestParam("slug") slug: String
  ) = {
    val fedId = federation.getId
    leagueService.findBySlug(fedId, slug).map {
      league =>
        new ModelAndView("admin/league/show")
          .addObject("league", league)
          .addObject("leagueName", i.t("League %s", league.leagueName))
          .addObject("createUrl", getUrl("LeagueAdminController.create"))
          .addObject("listUrl", getUrl("LeagueSeasonController.listLeagues"))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def create() = {
    val fedId = federation.getId

    // Placeholder for values
    val league = new League
    league.fedId = fedId

    val submitUrl = getUrl("LeagueAdminController.postCreate")
    val listLink = getUrl("LeagueSeasonController.listLeagues")

    new ModelAndView("admin/league/edit_form")
      .addObject("league", league)
      .addObject("listLink", listLink)
      .addObject("hiddens", Map().asJava)
      .addObject("action", i.t("Create league"))
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", "POST")
  }

  def postCreate(
    @Valid @ModelAttribute("league") league: League,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/league/edit_form")
        .addObject("action", i.t("Create league"))
        .addObject("listLink", getUrl("LeagueSeasonController.listLeagues"))
    }

    Try(leagueService.createLeague(federation.getId, league.leagueName, league.slug)) match {
      case Success(l) =>
        new ModelAndView("redirect:" + getUrl("LeagueSeasonController.listLeagues"))
      case Failure(e) =>
        result.addError(new FieldError("league", "slug", league.slug, true, Array(), Array(), "Duplicate slug"))
        new ModelAndView("admin/league/edit_form")
          .addObject("action", i.t("Create league"))
          .addObject("result", result)
          .addObject("league", league)
          .addObject("listLink", getUrl("LeagueSeasonController.listLeagues"))
    }
  }

  def edit(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    leagueService.findBySlug(federation.getId, slug).map {
      league =>
        val submitUrl = getUrl("LeagueAdminController.postEdit", "oldSlug" -> slug)

        val listLink = getUrl("LeagueSeasonController.listLeagues")
        // TODO handle notfound league (Option=None)

        new ModelAndView("admin/league/edit_form")
          .addObject("league", league)
          .addObject("listLink", listLink)
          .addObject("action", i.t("Edit league"))
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def postEdit(
    @RequestParam("oldSlug") slug: String,
    @Valid @ModelAttribute("league") league: League,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/league/edit_form")
        .addObject("action", i.t("Edit league"))
        .addObject("listLink", getUrl("LeagueSeasonController.listLeagues"))
    }

    val fedId = federation.getId

    val l1 = leagueService.modifyLeagueName(fedId, slug, league.leagueName)
    val l2 = leagueService.modifyLeagueSlug(fedId, slug, league.slug)

    if (l1.nonEmpty && l2.nonEmpty) {
      new ModelAndView("redirect:" + getUrl("LeagueSeasonController.listLeagues"))
    } else {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def delete(
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val fedId = federation.getId
    val listLink = getUrl("LeagueSeasonController.listLeagues")

    leagueService.findBySlug(fedId, slug).map {
      league =>
        new ModelAndView("admin/league/delete_confirm")
          .addObject("league", league)
          .addObject("listLink", listLink)
          .addObject("action", i.t("Delete league"))
          .addObject("submitUrl", getUrl("LeagueAdminController.postDelete"))
          .addObject("hiddens", Map("slug" -> slug).asJava)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def postDelete(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val result: Boolean = leagueService removeLeagueBySlug (federation.getId, slug)

    if (!result) {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League can't be removed"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist or has some attached seasons"))
    } else
      new ModelAndView("redirect:" + getUrl("LeagueSeasonController.listLeagues"))
  }
}
