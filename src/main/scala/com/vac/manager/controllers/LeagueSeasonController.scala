package com.vac.manager.controllers

import com.vac.manager.controllers.utils.Hyperlink
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ League, LeagueSeason }
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.util.Try

@Controller
class LeagueSeasonController extends UrlGrabber {

  class ActionableLeagueSeason(base: LeagueSeason) extends LeagueSeason with UrlGrabber {
    id = base.id
    val slug = id.league.slug
    val fedId = id.league.fedId

    @BeanProperty
    val year = id.seasonSlug

    def getLink() = {
      getUrl("LeagueSeasonController.showSeason", "slug" -> slug, "year" -> year)
    }
  }

  @Autowired
  var i: I18n = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  class LeagueWrapper(l: League) {
    val seasons = leagueService.findSeasonsByLeagueAsSeq(federation.getId, l.slug)
      .map(new ActionableLeagueSeason(_))

    val latest_season = Try(java.util.Collections.max(seasons.asJava, new java.util.Comparator[LeagueSeason] {
      override def compare(a: LeagueSeason, b: LeagueSeason): Int = {
        a.startTime.compareTo(b.startTime)
      }
    }))

    @BeanProperty
    val nonEmpty: Boolean = seasons.nonEmpty

    @BeanProperty
    val title: String = l.leagueName + " " +
      latest_season.map(_.id.seasonSlug)
      .getOrElse(i.t("(Not yet available)"))

    @BeanProperty
    val links: java.util.List[Hyperlink] = Map(
      i.t("Classification") -> "#classification",
      i.t("Calendar") -> "#calendar",
      i.t("Last matches played") -> "#matches",
      i.t("Current match day") -> "#matchday"
    ).toSeq.map {
        case (text, href) =>
          Hyperlink(text, href, "class")
      }.asJava

  }

  def listLeagues(_model: java.util.Map[String, Object]): String = {
    val model = _model.asScala
    val fedId = federation.getId

    val leagues = leagueService.findAllByFederation(fedId)
      .map(new LeagueWrapper(_))

    model += "competitions" -> leagues.asJavaCollection

    "league/season/listleagues"
  }

  def listSeasons(@PathVariable("slug") slug: String) = {
    val fedId = federation.getId()

    val league = leagueService.findSeasonsByLeague(fedId, slug)
    var seasons = null: java.util.Collection[ActionableLeagueSeason]

    val mav = league match {
      case None => new ModelAndView("league/season/notfound")
      case Some(league) => new ModelAndView("league/season/list")
    }

    if (league.nonEmpty)
      seasons = league.get.getSeasonList.asScala.map(new ActionableLeagueSeason(_)).asJava

    mav
      .addObject("league", league.orNull)
      .addObject("listLink", getUrl("LeagueController.list", "fedId" -> fedId))
      .addObject("seasons", seasons)
  }

  def showSeason(
    @PathVariable("slug") slug: String,
    @PathVariable("year") year: String) = {
    val fedId = federation.getId()

    val season = leagueService.findSeasonByLeagueSlug(fedId, slug, year)

    val mav: ModelAndView = new ModelAndView("league/season/show")
    mav
      .addObject("season", season.get)
      .addObject("teams", new java.util.ArrayList[AnyRef])
  }

}
