package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.service.game.GameService
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._
import scala.util.Try
import org.springframework.stereotype.Controller
import com.vac.manager.service.game.soccer.SoccerActService
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.service.team.TeamService
import com.vac.manager.model.game.Game
import org.springframework.web.bind.annotation.ModelAttribute
import com.vac.manager.model.game.soccer.SoccerAct
import com.vac.manager.controllers.actionable.ActionableSoccerAct
import javax.servlet.http.HttpServletRequest
import com.vac.manager.service.game.soccer.SoccerStaffStatsService
import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.controllers.actionable.ActionableSoccerStaffStats
import com.vac.manager.controllers.utils.ThymeleafList
import com.vac.manager.model.staff.Person

@Controller
class GameAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var gameService: GameService = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var soccerStaffStatsService: SoccerStaffStatsService = _

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
            .addObject("action", i.t("Create calendar"))
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
          Try(gameService.removeLeagueCalendarFromSeason(season)).recover {
            case e: IllegalArgumentException => throw new RuntimeException("League Season not found")
          }
          new ModelAndView("redirect:" + getUrl("GameController.list", "slug" -> slug, "year" -> year))
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def edit(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    request: HttpServletRequest): ModelAndView = {

    val fedId: Long = federation.getId

    // TODO select act by sport
    soccerActService.findGameAct(gameId).map {
      act =>
        {
          val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

          val submitMethod: String = "POST"
          val submitUrl: String = getUrl("GameAdminController.editPost", "slug" -> slug, "year" -> year, "gameId" -> gameId)
          val backLink: String = getUrl("GameController.show", "slug" -> slug, "year" -> year, "gameId" -> gameId)

          // TODO select act by sport
          val actFragment: String = "admin/game/soccer/edit_soccer"
          val actInstance: String = "edit_soccer"

          val teamsList: Seq[Team] = teamService.findTeamsByCompetitionId(1, fedId)

          val localStats: ThymeleafList[ActionableSoccerStaffStats] =
            new ThymeleafList(
              soccerStaffStatsService.findLocalStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId)).asJava)
          val visitorStats: ThymeleafList[ActionableSoccerStaffStats] =
            new ThymeleafList(
              soccerStaffStatsService.findVisitorStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId)).asJava)

          new ModelAndView("admin/game/edit")
            .addObject("action", "Edit")
            .addObject("act", new ActionableSoccerAct(act, slug, year, userCanEdit))
            .addObject("teamsList", teamsList.asJava)
            .addObject("localStats", localStats)
            .addObject("visitorStats", visitorStats)
            .addObject("actFragment", actFragment)
            .addObject("actInstance", actInstance)
            .addObject("submitMethod", submitMethod)
            .addObject("submitUrl", submitUrl)
            .addObject("calendarLink", backLink)
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def editPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @ModelAttribute("localStats") localStats: ThymeleafList[ActionableSoccerStaffStats],
    @ModelAttribute("visitorStats") visitorStats: ThymeleafList[ActionableSoccerStaffStats],
    @ModelAttribute act: SoccerAct) = {

    // TODO select act by sport
    soccerActService.editSoccerAct(act.actId, act.date, act.location, act.referees,
      act.localTeam.teamId, act.localResult, act.visitorTeam.teamId, act.visitorResult, act.incidents, act.signatures)

    "redirect:" +
      getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)
  }

  def callUpPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long) = {

    soccerStaffStatsService.callUpStaff(statsId)

    "redirect:" +
      getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)
  }

  def unCallUpPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long) = {

    soccerStaffStatsService.unCallUpStaff(statsId)

    "redirect:" +
      getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)
  }

}

