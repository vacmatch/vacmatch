package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ League, LeagueSeason, LeagueSeasonPK }
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.util.Layout
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import java.lang.Long
import java.util.ArrayList
import java.util.{ Calendar, Date, GregorianCalendar }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._
import com.vac.manager.model.competition.CompetitionMember
import com.vac.manager.controllers.actionable.ActionableCompetitionMember
import com.vac.manager.service.team.TeamService
import com.vac.manager.model.team.Team
import com.vac.manager.controllers.actionable.ActionableTeam
import javax.servlet.http.HttpServletRequest
import com.vac.manager.controllers.actionable.TeamEnrollLinks
import javax.validation.Valid
import org.springframework.validation.BindingResult
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.springframework.validation.FieldError

@Controller
@Layout("layouts/default_admin")
class LeagueSeasonAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var leagueService: LeagueService = _

  @Autowired
  var teamService: TeamService = _

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
      getUrl("GameController.showClassification", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getUserLink() = {
      getUrl("LeagueSeasonController.listSeasons", "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("LeagueSeasonAdminController.edit", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getEnrollLink() = {
      getUrl("LeagueSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getEditLeagueLink() = {
      getUrl("LeagueAdminController.edit", "slug" -> slug)
    }

    def getDeleteLink() = {
      getUrl("LeagueSeasonAdminController.delete", "slug" -> slug, "year" -> id.seasonSlug)
    }
  }

  def list(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val league = leagueService.findBySlug(federation.getId, slug)
    val seasons_ = Option(league.orElse(Option(new League)).get.seasonList)
      .orElse(Some(new ArrayList[LeagueSeason])).get

    val seasons = seasons_.asScala.map({ season => new CrudSeasonLeague(season) })

    new ModelAndView("admin/league/season/list")
      .addObject("league", league.orNull)
      .addObject("leagueName", league.map(l => i.t("League %s", l.leagueName)).orNull)
      .addObject("seasons", seasons.asJava)
      .addObject("createUrl", getUrl("LeagueSeasonAdminController.create", "slug" -> slug))
  }

  def create(
    @RequestParam("slug") leagueSlug: String
  ): ModelAndView = {

    val fedId = federation.getId

    leagueService.findBySlug(fedId, leagueSlug).map {
      l =>
        val leagueSeason = new LeagueSeason()
        val listLink = getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug)

        new ModelAndView("admin/league/season/edit_form")
          .addObject("leagueSeason", leagueSeason)
          .addObject("listLink", listLink)
          .addObject("submitUrl", getUrl("LeagueSeasonAdminController.postCreate"), "slug" -> leagueSlug)
          .addObject("action", i.t("Create Season"))
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League not found"))
        .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))
    }
  }

  def postCreate(
    @RequestParam("slug") leagueSlug: String,
    @Valid leagueSeason: LeagueSeason,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/league/season/edit_form")
        .addObject("action", i.t("Create Season"))
        .addObject("listLink", getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug))
    }

    val fedId = federation.getId

    Try(leagueService.createSeason(federation.getId, leagueSlug, leagueSeason.id.seasonSlug,
      leagueSeason.startTime, leagueSeason.endTime)) match {
      case Success(ls) =>
        new ModelAndView("redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug))
      case Failure(e) =>
        if (e.getCause().isInstanceOf[IllegalArgumentException])
          return new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Incorrect parameters"))
            .addObject("errorDescription", i.t("Start time and season slug aren't valid"))

        if (e.getCause().isInstanceOf[InstanceNotFoundException])
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("League not found"))
            .addObject("errorDescription", i.t("Sorry!, this league doesn't exist"))

        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("Unexpected error"))
          .addObject("errorDescription", e.getCause())
    }
  }

  @Autowired
  var conversionService: ConversionService = _

  def edit(
    @RequestParam("slug") leagueSlug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    leagueService.findSeasonByLeagueSlug(federation.getId, leagueSlug, year).map {
      leagueSeason =>

        val listLink = getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug)

        new ModelAndView("admin/league/season/edit_form")
          .addObject("leagueSeason", leagueSeason)
          .addObject("listLink", listLink)
          .addObject("submitUrl", getUrl("LeagueSeasonAdminController.postEdit", "slug" -> leagueSlug, "oldSeasonSlug" -> year))
          .addObject("action", i.t("Edit Season"))
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }

  }

  def postEdit(
    @RequestParam("slug") leagueSlug: String,
    @RequestParam("oldSeasonSlug") oldYear: String,
    @Valid leagueSeason: LeagueSeason,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/league/season/edit_form")
        .addObject("action", i.t("Edit Season"))
        .addObject("listLink", getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug))
    }

    val fedId = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, leagueSlug, oldYear).map {
      leagueSeason =>
        leagueService.modifySeasonStartTimeBySlug(fedId, leagueSlug, oldYear, leagueSeason.startTime)
        leagueService.modifySeasonEndTimeBySlug(fedId, leagueSlug, oldYear, leagueSeason.endTime)
        leagueService.modifySeasonYearBySlug(fedId, leagueSlug, oldYear, leagueSeason.id.seasonSlug)

        new ModelAndView("redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> leagueSlug))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def delete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    leagueService.findBySlug(federation.getId, slug).map {
      leagueSeason =>
        val listLink = getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
        val submitUrl = getUrl("LeagueSeasonAdminController.postDelete", "slug" -> slug, "year" -> year)
        val submitMethod = "POST"

        new ModelAndView("admin/league/season/delete_confirm")
          .addObject("hiddens", Map("slug" -> slug, "year" -> year).asJava)
          .addObject("listLink", listLink)
          .addObject("seasonName", year)
          .addObject("leagueName", leagueSeason.leagueName)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def postDelete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    var result = leagueService.removeSeasonBySlug(federation.getId, slug, year)

    if (result)
      new ModelAndView("redirect:" + getUrl("LeagueSeasonAdminController.list", "slug" -> slug))
    else
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
  }

  def enrollTeamInSeason(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      season =>
        val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

        val acceptUrl: String = getUrl("LeagueSeasonAdminController.list", "slug" -> slug)
        val submitUrl: String = getUrl("LeagueSeasonAdminController.enrollTeamInSeasonPost", "slug" -> slug, "year" -> year)
        val submitMethod: String = "POST"

        val sv = (slug, year)

        // TODO Modify -> find by federation (now it returns all teams)
        // All teams in federation
        val fullList: Seq[ActionableTeam with TeamEnrollLinks] =
          teamService.findTeamsByFederationId(fedId).map(team =>
            new ActionableTeam(team, hasPermissions) with TeamEnrollLinks {
              val (slug, year) = sv
            })

        // Only registered elements in league season
        val registeredList: Seq[ActionableCompetitionMember] =
          leagueService.findCompetitionMembersByLeagueSeasonId(season.id).map(cm => new ActionableCompetitionMember(cm, slug, year))

        new ModelAndView("admin/league/season/enrollTeam")
          .addObject("leagueSeason", season)
          .addObject("fullList", fullList.asJava)
          .addObject("registeredList", registeredList.asJava)
          .addObject("submitMethod", submitMethod)
          .addObject("submitUrl", submitUrl)
          .addObject("acceptUrl", acceptUrl)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }

  }

  def enrollTeamInSeasonPost(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    @RequestParam("teamId") teamId: Long
  ): ModelAndView = {

    val fedId: Long = federation.getId

    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      leagueSeason =>
        Try(leagueService.registerTeamInSeason(leagueSeason.id, teamId)) match {
          case Success(cm) =>
            new ModelAndView("redirect:" + getUrl("LeagueSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))
          case Failure(e) =>
            val cause: Throwable = e.getCause()

            if (cause.isInstanceOf[DuplicateInstanceException])
              return new ModelAndView("redirect:" +
                getUrl("LeagueSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))

            if ((cause.isInstanceOf[InstanceNotFoundException])) {
              if (e.asInstanceOf[InstanceNotFoundException].getClassName == "LeagueSeason")
                return new ModelAndView("error/show")
                  .addObject("errorTitle", i.t("League season not found"))
                  .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))

              if (e.asInstanceOf[InstanceNotFoundException].getClassName == "Team")
                return new ModelAndView("error/show")
                  .addObject("errorTitle", i.t("Team not found"))
                  .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))

            }
            new ModelAndView("error/show")
              .addObject("errorTitle", i.t("Unexpected error"))
              .addObject("errorDescription", e.getCause())
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }

  def disenrollTeamInSeasonPost(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    @RequestParam("teamId") teamId: Long
  ): ModelAndView = {

    val fedId: Long = federation.getId
    leagueService.findSeasonByLeagueSlug(fedId, slug, year).map {
      leagueSeason =>
        Try(leagueService.removeTeamFromSeason(leagueSeason.id, teamId)) match {
          case Success(cm) =>
            new ModelAndView("redirect:" +
              getUrl("LeagueSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))
          case Failure(e) =>
            new ModelAndView("error/show")
              .addObject("errorTitle", i.t("Competition member not found"))
              .addObject("errorDescription", i.t("Sorry!, this competition member doesn't exist"))
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("League season not found"))
        .addObject("errorDescription", i.t("Sorry!, this league season doesn't exist"))
    }
  }
}

