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

@Controller
@Layout("layouts/default_admin")
class LeagueAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

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

  def list() = {
    val fedId = federation.getId
    var leagues: Seq[CrudLeague] =
      leagueService findAllByFederation fedId map (new CrudLeague(_))

    new ModelAndView("admin/league/list")
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueAdminController.list"))
      .addObject("leagues", (leagues.asJava))
  }

  def show(
    @RequestParam("slug") slug: String
  ) = {
    val fedId = federation.getId
    val league = leagueService.findBySlug(fedId, slug)

    new ModelAndView("admin/league/show")
      .addObject("league", league.get)
      .addObject("leagueName", i.t("League %s", league.get.leagueName))
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueAdminController.list"))
  }

  def create() = {
    val fedId = federation.getId
    val leagueName = "Meow"
    val slug = "mw"
    val foundationalDate = null
    val sponsors: List[String] = null

    // Placeholder for values
    val league = new League
    league.fedId = fedId
    league.leagueName = leagueName
    league.slug = slug

    val submitUrl = getUrl("LeagueAdminController.postCreate")

    new ModelAndView("admin/league/edit_form")
      .addObject("league", league)
      .addObject("hiddens", Map().asJava)
      .addObject("action", i.t("Create league"))
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", "POST")
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueAdminController.list"))
  }

  def postCreate(
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val league = leagueService.createLeague(federation.getId, leagueName: String, slug)

    val mav = new ModelAndView("admin/league/show")

    mav.addObject("league", league)
  }

  def edit(
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val league = leagueService.findBySlug(federation.getId, slug)
    val submitUrl = getUrl("LeagueAdminController.postEdit")

    // TODO handle notfound league (Option=None)

    new ModelAndView("admin/league/edit_form")
      .addObject("league", league.get)
      .addObject("hiddens", Map("oldslug" -> slug).asJava)
      .addObject("action", i.t("Edit league"))
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", "POST")
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueAdminController.list"))
  }

  def postEdit(
    @RequestParam("oldslug") oldSlug: String,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): String = {
    val fedId = federation.getId

    leagueService modifyLeagueName (fedId, oldSlug, leagueName)
    leagueService modifyLeagueSlug (fedId, oldSlug, slug)

    val league = leagueService.findBySlug(fedId, slug)

    "redirect:" + getUrl("LeagueAdminController.list")
  }

  def delete(
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val fedId = federation.getId
    val league = leagueService.findBySlug(fedId, slug)

    new ModelAndView("admin/league/delete_confirm")
      .addObject("entityName", league)
      .addObject("action", i.t("Delete league"))
      .addObject("submitUrl", getUrl("LeagueAdminController.postDelete"))
      .addObject("hiddens", Map("slug" -> slug).asJava)
      .addObject("createUrl", getUrl("LeagueAdminController.create"))
      .addObject("listUrl", getUrl("LeagueAdminController.list"))
  }

  def postDelete(
    @RequestParam("slug") slug: String
  ): String = {

    val result = leagueService removeLeagueBySlug (federation.getId, slug)

    "redirect:" + getUrl("LeagueAdminController.list")

  }
}
