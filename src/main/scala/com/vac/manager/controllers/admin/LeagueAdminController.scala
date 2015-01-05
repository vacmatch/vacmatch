package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.Layout
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
  var leagueService: LeagueService = _

  class CrudLeague(base: League) extends League with UrlGrabber {

    fedId = base.fedId
    leagueName = base.leagueName
    slug = base.slug

    def getUserLink() = {
      getUrl("LeagueSeasonController.listSeasons", "fedId" -> fedId, "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("LeagueAdminController.edit", "fedId" -> fedId, "slug" -> slug)
    }

    def getDeleteLink() = {
      getUrl("LeagueAdminController.delete", "fedId" -> fedId, "slug" -> slug)
    }

    def getSeasonAdminLink() = {
      getUrl("LeagueSeasonAdminController.list", "fedId" -> fedId, "slug" -> slug)
    }
  }

  def list(@RequestParam("fedId") fedId: Long) = {
    var leagues: Seq[CrudLeague] =
      leagueService findAllByFederation fedId map (new CrudLeague(_))

    val mav: ModelAndView = new ModelAndView("admin/league/list")
    mav addObject ("createUrl", getUrl("LeagueAdminController.create", "fedId" -> fedId))
    mav addObject ("leagues", (leagues.asJava))
    mav
  }

  def show(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("slug") slug: String
  ) = {

    val league = leagueService.findBySlug(fedId, slug)

    val mav = new ModelAndView("admin/league/show")
    mav.addObject("league", league.get)
  }

  def create(@RequestParam("fedId") fedId: Long) = {

    var leagueName = "Meow"
    var slug = "mw"
    var foundationalDate = null
    var sponsors: List[String] = null

    // Placeholder for values
    val league = new League
    league.fedId = fedId
    league.leagueName = leagueName
    league.slug = slug

    val submitUrl = getUrl("LeagueAdminController.postCreate")

    val mav: ModelAndView = new ModelAndView("admin/league/edit_form")

    mav.addObject("league", league)
    mav.addObject("fedId", fedId)
    mav.addObject("hiddens", Map("fedId" -> fedId).asJava)
    mav.addObject("action", "Create League")
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", "POST")
  }

  def postCreate(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val league = leagueService.createLeague(fedId: Long, leagueName: String, slug)

    val mav = new ModelAndView("admin/league/show")

    mav.addObject("league", league)
  }

  def edit(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val league = leagueService.findBySlug(fedId, slug)
    val submitUrl = getUrl("LeagueAdminController.postEdit")

    // TODO handle notfound league (Option=None)

    val mav = new ModelAndView("admin/league/edit_form")

    mav.addObject("league", league.get)
    mav.addObject("hiddens", Map("fedId" -> fedId, "oldslug" -> slug).asJava)
    mav.addObject("action", "Edit league")
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", "POST")
  }

  def postEdit(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("oldslug") oldSlug: String,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): String = {

    leagueService modifyLeagueName (fedId, oldSlug, leagueName)
    leagueService modifyLeagueSlug (fedId, oldSlug, slug)

    val league = leagueService.findBySlug(fedId, slug)

    return "redirect:" + getUrl("LeagueAdminController.list", "fedId" -> fedId)
  }

  def delete(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    return new ModelAndView("admin/league/delete_confirm")
      .addObject("entity", "league")
      .addObject("submitUrl", getUrl("LeagueAdminController.postDelete"))
      .addObject("hiddens", Map("fedId" -> fedId, "slug" -> slug).asJava)
  }

  def postDelete(
    @RequestParam("fedId") fedId: Long,
    @RequestParam("slug") slug: String
  ): String = {

    val result = leagueService removeLeagueBySlug (fedId, slug)

    return "redirect:" + getUrl("LeagueAdminController.list", "fedId" -> fedId)

  }
}
