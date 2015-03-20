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
    @PathVariable("year") year: String): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        {
          val createCalendarLink: String = getUrl("GameAdminController.createCalendar", "slug" -> slug, "year" -> year)
          val deleteCalendarLink: String = getUrl("GameAdminController.deleteCalendar", "slug" -> slug, "year" -> year)

          val gamesMap: Map[Int, Seq[ActionableGame]] =
            gameService.findLeagueCalendar(season).map(game => new ActionableGame(game, slug, year)).groupBy(_.gameDay)

          val sortedGamesMap: SortedMap[Int, java.util.List[ActionableGame]] =
            SortedMap(gamesMap.toSeq: _*).map(element => (element._1, element._2.asJava))
          new ModelAndView("calendar/list")
            .addObject("gameDayList", sortedGamesMap.asJava)
            .addObject("createCalendarLink", createCalendarLink)
            .addObject("deleteCalendarLink", deleteCalendarLink)
        }
    }.getOrElse(throw new NoSuchElementException("League Season not found"))
  }

  def show(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String,
    @PathVariable("gameId") gameId: Long): ModelAndView = {

    val calendarLink: String = getUrl("GameController.list", "slug" -> slug, "year" -> year)

    gameService.find(gameId).map {
      game =>
        {
          new ModelAndView("game/show")
            .addObject("game", game)
            .addObject("calendarLink", calendarLink)

        }
    }.getOrElse(throw new NoSuchElementException("Game not found"))
  }

}

