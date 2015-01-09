package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ League, LeagueSeason, LeagueSeasonPK }
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.Layout
import com.vac.manager.util.FederationBean
import java.lang.Long
import java.util.{ Calendar, Date, GregorianCalendar }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._

@Controller
@Layout("layouts/default_admin")
class LeagueSeasonAdminController extends UrlGrabber {

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var federation: FederationBean = _

  class CrudSeasonLeague(base: LeagueSeason) extends LeagueSeason with UrlGrabber {

    id = base.id
    startTime = base.startTime
    endTime = base.endTime

    val fedId = id.getLeague().getFedId()
    val slug = id.getLeague().getSlug()

    def getName(): String = {
      return id.getSeasonSlug()
    }

    def getLink() = {
      getUrl("LeagueSeasonController.listSeasons", "slug" -> slug)
    }

    def getUserLink() = {
      getUrl("LeagueSeasonController.listSeasons", "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("LeagueSeasonAdminController.edit", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getEditLeagueLink() = {
      getUrl("LeagueAdminController.edit", "slug" -> slug)
    }

    def getDeleteLink() = {
      getUrl("LeagueSeasonAdminController.delete", "slug" -> slug)
    }
  }

  def list(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val league = leagueService.findBySlug(federation getId, slug)
    val seasons_ = league.orElse(Option(new League)).get.seasonList

    val seasons = seasons_.asScala.map({ season => new CrudSeasonLeague(season) })

    return new ModelAndView("admin/league/season/list")
      .addObject("league", league.orNull)
      .addObject("seasons", seasons.asJava)
      .addObject("createUrl", getUrl("LeagueSeasonAdminController.create", "slug" -> slug))
  }

  def create(
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val leagueSeason = new LeagueSeason()

    return new ModelAndView("admin/league/season/edit_form")
      .addObject("leagueSeason", leagueSeason)
      .addObject("submitUrl", getUrl("LeagueSeasonAdminController.postCreate"))
      .addObject("hiddens", Map("slug" -> slug).asJava)
      .addObject("action", "Create Season")
      .addObject("submitMethod", "POST")
  }

  def postCreate(
    @RequestParam("slug") slug: String,
    @RequestParam("seasonSlug") year: String,
    @RequestParam("startTime") startTime: Date,
    @RequestParam("endTime") endTime: Date
  ): String = {

    //val df = new SimpleDateFormat("yyyy/MM/dd")
    //val startTime = df.parse(startTimeStr)
    //val endTime = df.parse(endTimeStr)

    val start: Calendar = new GregorianCalendar()
    start.setTime(startTime)
    val end: Calendar = new GregorianCalendar()
    end.setTime(endTime)

    println("CREATING STUFF AT " + startTime)

    leagueService.createSeason(federation getId, slug, year, start, end)

    return "redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
  }

  @Autowired
  var conversionService: ConversionService = _

  def edit(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    println("Convertor is installed correctly == " + conversionService.canConvert(classOf[Date], classOf[String]))
    println("Convertor is installed correctly == " + conversionService.canConvert(classOf[Calendar], classOf[String]))
    println("Convertor is installed correctly == " + conversionService.canConvert(classOf[GregorianCalendar], classOf[String]))

    val leagueSeason = leagueService.findSeasonByLeagueSlug(federation getId, slug, year)

    val startTimeStr = conversionService.convert(leagueSeason.get.getStartTime(), classOf[String])
    val endTimeStr = conversionService.convert(leagueSeason.get.getEndTime(), classOf[String])

    println("Convertor has values = " + startTimeStr + " and " + endTimeStr)

    return new ModelAndView("admin/league/season/edit_form")
      .addObject("leagueSeason", leagueSeason.orNull)
      .addObject("startTime", startTimeStr)
      .addObject("endTime", endTimeStr)
      .addObject("submitUrl", getUrl("LeagueSeasonAdminController.postEdit"))
      .addObject("hiddens", Map("slug" -> slug, "oldSeasonSlug" -> year).asJava)
      .addObject("action", "Edit Season")
      .addObject("submitMethod", "POST")
  }
  def postEdit(
    @RequestParam("slug") slug: String,
    @RequestParam("oldSeasonSlug") oldYear: String,
    @RequestParam("seasonSlug") year: String,
    @RequestParam("startTime") startTime: Date,
    @RequestParam("endTime") endTime: Date
  ): String = {

    val fedId = federation getId
    val start = new GregorianCalendar()
    start.setTime(startTime)
    val end = new GregorianCalendar()
    end.setTime(endTime)

    leagueService.modifySeasonStartTimeBySlug(fedId, slug, oldYear, start)
    leagueService.modifySeasonEndTimeBySlug(fedId, slug, oldYear, end)
    val r = leagueService.modifySeasonYearBySlug(fedId, slug, oldYear, year)

    println("MODIFIED SEASON PREV = " + oldYear + " NOW IS " + r.orNull + r.get.id.getSeasonSlug() + " / " + year)

    return "redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
    return "redirect:" + getUrl("LeagueSeasonAdminController.edit", "slug" -> slug, "year" -> year)
  }
  def delete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    return new ModelAndView("admin/league/season/delete_confirm")
      .addObject("hiddens", Map("slug" -> slug, "year" -> year).asJava)

  }
  def postDelete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): String = {

    var result = leagueService.removeSeasonBySlug(federation getId, slug, year)

    return "redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
  }
}
