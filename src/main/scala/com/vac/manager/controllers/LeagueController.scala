package com.vac.manager.controllers

import java.util.Calendar
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League
import com.vac.manager.service.competition.LeagueService
import org.resthub.web.springmvc.router.Router
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
class LeagueController() extends UrlGrabber {

  @Autowired
  var leagueService: LeagueService = _

  class ActionableLeague(base: League) extends League with UrlGrabber {

    fedId = base.fedId
    leagueName = base.leagueName
    slug = base.slug

    def getEditLink() = {
      getUrl("LeagueController.edit", "slug" -> slug) + "?fedId=" + fedId
    }

    def getDeleteLink() = {
      getUrl("LeagueController.delete", "slug" -> slug) + "?fedId=" + fedId
    }
  }

  def list(@RequestParam("fedId") fedId: Int) = {
    var leagues: Seq[League] = leagueService
      .findActiveByFederation(fedId).map({ season => season.id.league })
      .map({ league => new ActionableLeague(league) })

    val slugs = leagues.map({ league => league.getSlug() })
    leagues = leagues.filter({league => !slugs.contains(league.getSlug()) })

    val mav: ModelAndView = new ModelAndView("league/list")
    mav.addObject("leagues", leagues.asJava)
    mav.addObject("d", fedId)
    mav
  }

  def show(@RequestParam("fedId") fedId: Int,
    @PathVariable("slug") slug: String) = {

    val league = leagueService.findBySlug(fedId, slug)

    val mav = new ModelAndView("league/show")
    mav.addObject("league", league.get)
  }


  def create(@RequestParam("fedId") fedId: Int) = {

    var leagueName = "Meow"
    var slug = "mw"
    var foundationalDate = null
    var sponsors: List[String] = null

    // Placeholder for values
    val league = new League
    league.fedId = fedId
    league.leagueName = leagueName
    league.slug = slug

    val submitUrl = getUrl("LeagueController.createPost")

    val mav: ModelAndView = new ModelAndView("league/edit_form")

    mav.addObject("league", league)
    mav.addObject("fedId", fedId)
    mav.addObject("action", "Create League")
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", "POST")
  }

  def createPost(
    @RequestParam("fedId") fedId: Int,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val league = leagueService.createLeague(fedId: Int, leagueName: String, slug)

    val mav = new ModelAndView("league/show")

    mav.addObject("league", league)
  }

  def edit(
    @RequestParam("fedId") fedId: Int,
    @PathVariable("slug") slug: String
  ): ModelAndView = {
    val league = leagueService.findBySlug(fedId, slug)
    val submitUrl = getUrl("LeagueController.editPost", "slug" -> slug)

    // TODO handle notfound league (Option=None)

    val mav = new ModelAndView("league/edit_form")

    mav.addObject("league", league.get)
    mav.addObject("fedId", fedId)
    mav.addObject("action", "Edit league")
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", "POST")
  }

  def editPost(
    @RequestParam("fedId") fedId: Int,
    @RequestParam("leagueName") leagueName: String,
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val league = leagueService.createLeague(fedId, leagueName, slug)

    val mav = new ModelAndView("league/show")
    mav.addObject("league", league)
  }

}
