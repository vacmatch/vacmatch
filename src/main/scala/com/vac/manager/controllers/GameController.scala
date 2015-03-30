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
import com.vac.manager.model.game.Game
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import java.util.ArrayList
import java.util.HashMap
import scala.collection.SortedMap
import com.vac.manager.controllers.actionable.ActionableGame
import javax.servlet.http.HttpServletRequest

@Controller
class GameController extends UrlGrabber {

  @Autowired
  var gameService: GameService = _

  @Autowired
  var leagueService: LeagueService = _

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
          val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

          // Authenticated actions on menu

          val authenticatedActionsMenu: Map[String, String] = Map(
            "Create calendar" -> getUrl("GameAdminController.createCalendar", "slug" -> slug, "year" -> year),
            "Delete calendar" -> getUrl("GameAdminController.deleteCalendar", "slug" -> slug, "year" -> year)
          )

          val actionsMenu = if (userCanEdit) authenticatedActionsMenu else Map()

          // Get calendar games grouped by matchDay
          val gamesMap: Map[Int, Seq[ActionableGame]] =
            gameService.findLeagueCalendar(season).map(game =>
              new ActionableGame(game, slug, year, userCanEdit)).groupBy(_.matchDay)

          // Sort calendar by matchDay
          val sortedGamesMap: SortedMap[Int, java.util.List[ActionableGame]] =
            SortedMap(gamesMap.toSeq: _*).map(element => (element._1, element._2.asJava))

          new ModelAndView("calendar/list")
            .addObject("actionsMenu", actionsMenu.asJava.entrySet)
            .addObject("gameDayList", sortedGamesMap.asJava)
        }
      }.getOrElse(throw new NoSuchElementException("League Season not found"))
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
        {
          // Check user permissions
          val isAuthenticated: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")
          val actions: Map[String, Boolean] =
            Map("showGameButtons" -> isAuthenticated)

          new ModelAndView("game/show")
            .addObject("actions", actions.asJava)
            .addObject("game", game)
            .addObject("calendarLink", calendarLink)

        }
    }.getOrElse(throw new NoSuchElementException("Game not found"))
  }

}

