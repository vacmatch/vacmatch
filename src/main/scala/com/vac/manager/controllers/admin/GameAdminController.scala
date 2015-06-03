package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
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
import com.vac.manager.model.staff.Person
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import scala.util.Success
import scala.util.Failure
import javax.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError

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
    @PathVariable("year") year: String
  ): ModelAndView = {

    val fedId: Long = federation.getId
    val submitMethod: String = "POST"
    val submitUrl: String = getUrl("GameAdminController.createCalendarPost", "slug" -> slug, "year" -> year)
    val showCalendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          new ModelAndView("admin/calendar/edit")
            .addObject("action", i.t("Create calendar"))
            .addObject("submitMethod", submitMethod)
            .addObject("submitUrl", submitUrl)
            .addObject("calendarLink", showCalendarLink)
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def createCalendarPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @RequestParam(value = "teamsNumber", defaultValue = "0") teamsNumber: Int,
    @RequestParam(value = "leagueRounds", defaultValue = "0") leagueRounds: Int
  ): ModelAndView = {

    val fedId: Long = federation.getId

    // Gets the season
    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        Try(gameService.createLeagueCalendar(season, teamsNumber, leagueRounds)) match {
          case Success(g) =>
            new ModelAndView("redirect:" + getUrl("GameController.list", "slug" -> slug, "year" -> year))

          case Failure(e) =>
            val mv = new ModelAndView("error/show")
            val cause: Throwable = e.getCause()
            cause match {
              case inf: InstanceNotFoundException =>
                mv.addObject("errorTitle", i.t("League season not found"))
                  .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
              case iae: IllegalArgumentException =>
                mv.addObject("errorTitle", i.t("Incorrect values"))
                  .addObject("errorDescription", i.t("Values not valid for create calendar."))
                  .addObject("backText", i.t("Back to create"))
                  .addObject("backLink", getUrl("GameAdminController.createCalendar", "slug" -> slug, "year" -> year))
              case die: DuplicateInstanceException =>
                mv.addObject("errorTitle", i.t("Duplicate calendar"))
                  .addObject("errorDescription", i.t("There's a calendar for this league season."))
                  .addObject("backText", i.t("Go to calendar"))
                  .addObject("backLink", getUrl("GameController.list", "slug" -> slug, "year" -> year))
              case _ =>
                mv.addObject("errorTitle", i.t("Unexpected error"))
                  .addObject("errorDescription", cause)
            }
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def deleteCalendar(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ): ModelAndView = {

    val fedId: Long = federation.getId
    val submitMethod: String = "POST"
    val submitUrl: String = getUrl("GameAdminController.deleteCalendarPost", "slug" -> slug, "year" -> year)
    val calendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          new ModelAndView("admin/calendar/delete")
            .addObject("submitMethod", submitMethod)
            .addObject("submitUrl", submitUrl)
            .addObject("calendarLink", calendarLink)
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def deleteCalendarPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String
  ): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        Try(gameService.removeLeagueCalendarFromSeason(season)) match {
          case Success(_) =>
            new ModelAndView("redirect:" + getUrl("GameController.list", "slug" -> slug, "year" -> year))

          case Failure(e) =>
            val mv = new ModelAndView("error/show")
            val cause: Throwable = e.getCause()
            cause match {
              case e: InstanceNotFoundException =>
                e.getClassName match {
                  case "LeagueSeason" =>
                    mv.addObject("errorTitle", i.t("League season not found"))
                      .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
                  case "SoccerAct" =>
                    mv.addObject("errorTitle", i.t("Soccer act not found"))
                      .addObject("errorDescription", i.t("Sorry!, a soccer act from this calendar doesn't exist"))
                  case _ =>
                    mv.addObject("errorTitle", i.t("Unexpected error"))
                      .addObject("errorDescription", cause)
                }
              case _ =>
                mv.addObject("errorTitle", i.t("Unexpected error"))
                  .addObject("errorDescription", cause)
            }
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def edit(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    // TODO select act by sport
    soccerActService.findGameAct(gameId).map {
      act =>
        val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

        val submitMethod: String = "POST"
        val submitUrl: String = getUrl("GameAdminController.editPost", "slug" -> slug, "year" -> year, "gameId" -> gameId)
        val backLink: String = getUrl("GameController.show", "slug" -> slug, "year" -> year, "gameId" -> gameId)

        // TODO select act by sport
        val actFragment: String = "admin/game/soccer/edit_soccer"

        leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
          leagueSeason =>
            val teamsList: Seq[Team] = teamService.findTeamsByLeagueSeasonId(leagueSeason.id)

            val localPlayerStats: Seq[ActionableSoccerStaffStats] =
              soccerStaffStatsService.findLocalPlayersStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
              )

            val localStaffStats: Seq[ActionableSoccerStaffStats] =
              soccerStaffStatsService.findLocalStaffStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
              )

            val visitorPlayerStats: Seq[ActionableSoccerStaffStats] =
              soccerStaffStatsService.findVisitorPlayersStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
              )

            val visitorStaffStats: Seq[ActionableSoccerStaffStats] =
              soccerStaffStatsService.findVisitorStaffStats(act.actId).map(
                staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
              )

            new ModelAndView("admin/game/edit")
              .addObject("action", "Edit")
              .addObject("act", new ActionableSoccerAct(act, slug, year, userCanEdit))
              .addObject("teamsList", teamsList.asJava)
              .addObject("localPlayerStats", localPlayerStats.asJava)
              .addObject("visitorPlayerStats", visitorPlayerStats.asJava)
              .addObject("localStaffStats", localStaffStats.asJava)
              .addObject("visitorStaffStats", visitorStaffStats.asJava)
              .addObject("actFragment", actFragment)
              .addObject("submitMethod", submitMethod)
              .addObject("submitUrl", submitUrl)
              .addObject("calendarLink", backLink)
        }.getOrElse {
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("League season not found"))
            .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Soccer act not found"))
        .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
    }
  }

  def editPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @Valid @ModelAttribute act: SoccerAct,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Incorrect values"))
        .addObject("errorDescription", i.t("Values not valid in edit act. Date format must be dd/mm/yyyy hh:hh"))
        .addObject("backText", i.t("Back"))
        .addObject("backLink", getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))
    }

    // TODO select act by sport
    Try(soccerActService.editSoccerAct(act.actId, act.date, act.location, act.referees,
      act.localTeam.teamId, act.localResult, act.visitorTeam.teamId, act.visitorResult,
      act.incidents, act.signatures)) match {
      case Success(st) =>
        new ModelAndView("redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            e.getClassName match {
              case "SoccerAct" =>
                mv.addObject("errorTitle", i.t("Soccer act not found"))
                  .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
              case "Team" =>
                mv.addObject("errorTitle", i.t("Team not found"))
                  .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
              case _ =>
                mv.addObject("errorTitle", i.t("Unexpected error"))
                  .addObject("errorDescription", cause)
            }
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def editStatsPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: java.lang.Long,
    @RequestParam(value = "goals", defaultValue = "0") goalsNumber: Int,
    @RequestParam("staffStats") staffStats: java.util.List[String]
  ): ModelAndView = {

    val firstYellowCard: Calendar = if (staffStats.indexOf("firstYellowCard") >= 0) Calendar.getInstance() else null
    val secondYellowCard: Calendar = if (staffStats.indexOf("secondYellowCard") >= 0) Calendar.getInstance() else null
    val redCard: Calendar = if (staffStats.indexOf("redCard") >= 0) Calendar.getInstance() else null
    val goals: Seq[Calendar] = {
      var list: Seq[Calendar] = List()
      for (e <- 1 to goalsNumber)
        list = list :+ Calendar.getInstance()
      list
    }

    // TODO select stats by sport
    Try(soccerStaffStatsService.editStats(statsId, firstYellowCard, secondYellowCard,
      redCard, goals)) match {
      case Success(st) =>
        new ModelAndView("redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause: Throwable = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer staff stats not found"))
              .addObject("errorDescription", i.t("Sorry!, these stats don't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def editRestPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @ModelAttribute act: SoccerAct
  ): ModelAndView = {

    // TODO select act by sport
    Try(soccerActService.editRestSoccerAct(gameId, act.localTeam.teamId)) match {
      case Success(st) =>
        new ModelAndView("redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer act not found"))
              .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def callUpPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long
  ): ModelAndView = {

    Try(soccerStaffStatsService.callUpStaff(statsId)) match {
      case Success(st) =>
        new ModelAndView("redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer staff stats not found"))
              .addObject("errorDescription", i.t("Sorry!, these stats don't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def unCallUpPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long
  ): ModelAndView = {

    Try(soccerStaffStatsService.unCallUpStaff(statsId)) match {
      case Success(st) =>
        new ModelAndView("redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId))

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer staff stats not found"))
              .addObject("errorDescription", i.t("Sorry!, these stats don't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def setStaffPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long,
    @RequestParam("staffPosition") staffPosition: String
  ) = {

    Try(soccerStaffStatsService.setStaff(statsId, staffPosition)) match {
      case Success(st) =>
        "redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer staff stats not found"))
              .addObject("errorDescription", i.t("Sorry!, these stats don't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def unSetStaffPost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long,
    @RequestParam("statsId") statsId: Long
  ) = {

    Try(soccerStaffStatsService.unSetStaff(statsId)) match {
      case Success(st) =>
        "redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer staff stats not found"))
              .addObject("errorDescription", i.t("Sorry!, these stats don't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def setRestStatePost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long
  ) = {

    Try(soccerActService.setRestState(gameId)) match {
      case Success(st) =>
        "redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer act not found"))
              .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

  def unSetRestStatePost(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: java.lang.Long
  ) = {

    Try(soccerActService.unSetRestState(gameId)) match {
      case Success(st) =>
        "redirect:" +
          getUrl("GameAdminController.edit", "slug" -> slug, "year" -> year, "gameId" -> gameId)

      case Failure(e) =>
        val mv = new ModelAndView("error/show")
        val cause = e.getCause()
        cause match {
          case e: InstanceNotFoundException =>
            mv.addObject("errorTitle", i.t("Soccer act not found"))
              .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
          case _ =>
            mv.addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", cause)
        }
    }
  }

}

