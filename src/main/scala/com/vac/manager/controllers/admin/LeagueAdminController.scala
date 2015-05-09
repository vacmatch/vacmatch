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
    @RequestParam("slug") slug: String) = {
    val fedId = federation.getId
    val league = leagueService.findBySlug(fedId, slug)

    new ModelAndView("admin/league/show")
      .addObject("league", league.get)
      .addObject("leagueName", i.t("League %s", league.get.leagueName))
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueSeasonController.listLeagues"))
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
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueSeasonController.listLeagues"))
  }

  def postCreate(
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String): ModelAndView = {
    val league = leagueService.createLeague(federation.getId, leagueName: String, slug)

    val mav = new ModelAndView("admin/league/show")

    mav.addObject("league", league)
  }

  def edit(
    @RequestParam("slug") slug: String): ModelAndView = {
    val league = leagueService.findBySlug(federation.getId, slug)
    val submitUrl = getUrl("LeagueAdminController.postEdit")

    val listLink = getUrl("LeagueSeasonController.listLeagues")
    // TODO handle notfound league (Option=None)

    new ModelAndView("admin/league/edit_form")
      .addObject("league", league.get)
      .addObject("listLink", listLink)
      .addObject("hiddens", Map("oldslug" -> slug).asJava)
      .addObject("action", i.t("Edit league"))
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", "POST")
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueSeasonController.listLeagues"))
  }

  def postEdit(
    @RequestParam("oldslug") oldSlug: String,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String): String = {
    val fedId = federation.getId

    leagueService modifyLeagueName (fedId, oldSlug, leagueName)
    leagueService modifyLeagueSlug (fedId, oldSlug, slug)

    val league = leagueService.findBySlug(fedId, slug)

    "redirect:" + getUrl("LeagueSeasonController.listLeagues")
  }

  def delete(
    @RequestParam("slug") slug: String): ModelAndView = {
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
          .addObject("createUrl", getUrl("LeagueAdminController.create"))
          .addObject("listUrl", getUrl("LeagueSeasonController.listLeagues"))
    }.getOrElse(throw new NoSuchElementException("League not found"))
  }

  def postDelete(
    @RequestParam("slug") slug: String): String = {

    val result = leagueService removeLeagueBySlug (federation.getId, slug)

    "redirect:" + getUrl("LeagueSeasonController.listLeagues")

  }
}
