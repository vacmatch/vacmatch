package com.vac.manager.controllers.admin

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import com.vac.manager.controllers.utils.UrlGrabber
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.game.GameService
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.FederationBean
import scala.collection.JavaConverters._
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import scala.util.Try
import org.springframework.stereotype.Controller

@Controller
class GameAdminController extends UrlGrabber {

  @Autowired
  var gameService: GameService = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  def createCalendar(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String): ModelAndView = {

    val fedId: Long = federation.getId
    val submitMethod: String = "POST"
    val submitUrl: String = getUrl("GameAdminController.createCalendarPost", "slug" -> slug, "year" -> year)
    val showCalendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          new ModelAndView("admin/calendar/edit")
            .addObject("action", "Create")
            .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet())
            .addObject("submitMethod", submitMethod)
            .addObject("submitUrl", submitUrl)
            .addObject("calendarLink", showCalendarLink)
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def createCalendarPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @RequestParam("teamsNumber") teamsNumber: Int,
    @RequestParam("leagueRounds") leagueRounds: Int): ModelAndView = {

    val fedId: Long = federation.getId

    // Gets the season
    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          Try(gameService.createLeagueCalendar(season, teamsNumber, leagueRounds)).recover {
            case iae: IllegalArgumentException => throw new RuntimeException("Illegal values to create calendar")
            case die: DuplicateInstanceException => throw new RuntimeException("Existing Calendar")
          }
          new ModelAndView("redirect:" + getUrl("GameController.list", "slug" -> slug, "year" -> year))
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def deleteCalendar(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String): ModelAndView = {

    val fedId: Long = federation.getId
    val submitMethod: String = "POST"
    val submitUrl: String = getUrl("GameAdminController.deleteCalendarPost", "slug" -> slug, "year" -> year)
    val calendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          new ModelAndView("admin/calendar/delete")
            .addObject("action", "Delete")
            .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet())
            .addObject("submitMethod", submitMethod)
            .addObject("submitUrl", submitUrl)
            .addObject("calendarLink", calendarLink)
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def deleteCalendarPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          gameService.removeLeagueCalendarFromSeason(season)
          new ModelAndView("redirect:" + getUrl("GameController.list", "slug" -> slug, "year" -> year))
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

}

