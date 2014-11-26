package main.scala.controllers

import java.util.Calendar
import main.scala.model.competition.League
import main.scala.service.competition.LeagueService
import org.resthub.web.springmvc.router.HTTPRequestAdapter
import org.resthub.web.springmvc.router.Router
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._

@Controller
class LeagueController() {

  @Autowired
  var leagueService: LeagueService = _

  def list(@RequestParam("fedId") fedId: Int) = {
    val leagues: Seq[League] = leagueService.findActiveByFederation(fedId)

    val mav: ModelAndView = new ModelAndView("league/list")
    mav.addObject("leagues", leagues.asJava)
    mav.addObject("d", fedId)
    mav
  }

  def create(@RequestParam("fedId") fedId: Int) = {

    var leagueName: String = "Meow"
    var slug: String = "mw"
    var foundationalDate: Calendar = null
    var sponsors: List[String] = null

    val league: League = leagueService.createLeague(fedId, leagueName, slug)
    val submitUrl = Router.getFullUrl("LeagueController.createPost")

    val mav: ModelAndView = new ModelAndView("league/edit_form");

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
    @PathVariable slug: String
  ): ModelAndView = {
    val league = leagueService.findBySlug(fedId, slug)
    val submitUrl = Router.getFullUrl("LeagueController.editPost")

    val mav = new ModelAndView("league/edit_form")

    mav.addObject("league", league)
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
