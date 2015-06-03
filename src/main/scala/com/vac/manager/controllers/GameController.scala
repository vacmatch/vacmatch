package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.game.GameService
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.FederationBean
import com.vac.manager.model.competition.LeagueSeasonPK
import com.vac.manager.controllers.utils.UrlGrabber
import org.springframework.web.bind.annotation.RequestParam
import com.vac.manager.model.competition.LeagueSeason
import scala.collection.JavaConverters._
import com.vac.manager.model.game.{ Game }
import com.vac.manager.model.game.soccer.SoccerAct
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import java.util.ArrayList
import java.util.HashMap
import scala.collection.SortedMap
import com.vac.manager.controllers.actionable.ActionableGame
import javax.servlet.http.HttpServletRequest
import com.vac.manager.service.game.soccer.SoccerStaffStatsService
import com.vac.manager.model.game.soccer.SoccerStaffStats
import com.vac.manager.controllers.actionable.ActionableSoccerAct
import com.vac.manager.service.game.soccer.SoccerActService
import com.vac.manager.controllers.actionable.ActionableSoccerStaffStats
import com.vac.manager.model.game.SoccerClassificationEntry
import com.vacmatch.util.i18n.I18n

@Controller
class GameController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var gameService: GameService = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var soccerStatsService: SoccerStaffStatsService = _

  @Autowired
  var federation: FederationBean = _

  def list(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year)
      .map { season =>
        {
          // Check user permissions
          val userCanEdit: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

          // Authenticated actions on menu
          val authenticatedActionsMenu: Map[String, String] = Map(
            "Create calendar" -> getUrl("GameAdminController.createCalendar", "slug" -> slug, "year" -> year),
            "Delete calendar" -> getUrl("GameAdminController.deleteCalendar", "slug" -> slug, "year" -> year)
          )

          val actionsMenu: Map[String, String] = if (userCanEdit) authenticatedActionsMenu else Map()

          // TODO Get diferent act depending on the sport
          val actsMap: Map[Int, Seq[ActionableSoccerAct]] =
            soccerActService.findLeagueSoccerActs(season).map(act =>
              new ActionableSoccerAct(act, slug, year, userCanEdit)).groupBy(_.game.matchDay)

          val sortedActsMap: SortedMap[Int, java.util.List[ActionableSoccerAct]] =
            SortedMap(actsMap.toSeq: _*).map(element => (element._1, element._2.asJava))

          val actFragment: String = "calendar/soccer/listSoccer"

          new ModelAndView("calendar/list")
            .addObject("actionsMenu", actionsMenu.asJava.entrySet)
            .addObject("matchDayList", sortedActsMap.asJava)
            .addObject("actFragment", actFragment)
        }
      }.getOrElse {
        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("League season not found"))
          .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
      }
  }

  def show(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: Long,
    request: HttpServletRequest
  ): ModelAndView = {

    val calendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    gameService.find(gameId).map {
      game =>
        // Check user permissions
        val userCanEdit: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

        // TODO Get diferent act depending on the sport
        soccerActService.findGameAct(gameId).map {
          act =>
            {
              // Players stats
              val localStats: Seq[SoccerStaffStats] = soccerStatsService.findLocalPlayersStats(act.actId)
              val visitorStats: Seq[SoccerStaffStats] = soccerStatsService.findVisitorPlayersStats(act.actId)

              // Staff stats
              val localStaff: Seq[ActionableSoccerStaffStats] =
                soccerStatsService.findLocalStaffStats(act.actId).map(
                  staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
                )
              val visitorStaff: Seq[ActionableSoccerStaffStats] =
                soccerStatsService.findVisitorStaffStats(act.actId).map(
                  staffStats => new ActionableSoccerStaffStats(staffStats, slug, year, gameId, userCanEdit)
                )

              val calendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

              new ModelAndView("game/show")
                .addObject("act", new ActionableSoccerAct(act, slug, year, userCanEdit))
                .addObject("localStats", localStats.asJava)
                .addObject("visitorStats", visitorStats.asJava)
                .addObject("localStaff", localStaff.asJava)
                .addObject("visitorStaff", visitorStaff.asJava)
                .addObject("calendarLink", calendarLink)
            }
        }.getOrElse {
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Soccer act not found"))
            .addObject("errorDescription", i.t("Sorry!, this soccer act doesn't exist"))
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Game not found"))
        .addObject("errorDescription", i.t("Sorry!, this game doesn't exist"))
    }
  }

  def showClassification(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    val sportFragment: String = "classification/soccer/show_soccer"
    val sportInstance: String = "show_soccer"

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      leagueSeason =>
        val leagueClassification: Seq[SoccerClassificationEntry] =
          gameService.getLeagueClassification(leagueSeason)

        new ModelAndView("classification/show")
          .addObject("leagueClassification", leagueClassification.asJava)
          .addObject("leagueSeason", leagueSeason)
          .addObject("sportFragment", sportFragment)
          .addObject("sportInstance", sportInstance)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

}

