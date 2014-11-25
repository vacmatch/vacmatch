package main.scala.controllers

import org.springframework.stereotype.Controller
import main.scala.service.competition.LeagueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.util.Calendar
import main.scala.model.competition.League
import scala.collection.JavaConverters._
import org.resthub.web.springmvc.router.Router

@Controller
class LeagueController() {

  @Autowired
  var leagueService: LeagueService = _

  def list(@RequestParam("fedId") fedId: Int) = {
    val leagues: Seq[League] = leagueService.findActiveByFederation(fedId)

    val mav: ModelAndView = new ModelAndView("league/list");
    mav.addObject("leagues", leagues.asJava)
    mav.addObject("d", fedId)
    mav
  }

  def create(@RequestParam("fedId") fedId: Int): ModelAndView = {

    var leagueName: String = null
    var foundationalDate: Calendar = null
    var sponsors: List[String] = null

    var league: League = leagueService.createLeague(fedId, leagueName)

    var mav: ModelAndView = new ModelAndView("league/show");
    mav.addObject("league", league);
    mav.addObject("submitUrl", Router.reverse("leagueController.createPost").url)

    return mav
  }

  def createPost(@RequestParam("fedId") fedId: Int,
    @RequestParam("leagueName") leagueName: String) : ModelAndView = {

    var league = leagueService.createLeague(fedId:Int, leagueName:String)

    var mav = new ModelAndView("league/show")
    mav.addObject("league", league)

    return mav
  }

}
