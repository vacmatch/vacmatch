package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.{ Competition, CompetitionSeason, CompetitionSeasonPK }
import com.vac.manager.service.competition.CompetitionService
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
class CompetitionSeasonAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var federation: FederationBean = _

  class CrudSeasonCompetition(base: CompetitionSeason) extends CompetitionSeason with UrlGrabber {

    id = base.id
    startTime = base.startTime
    endTime = base.endTime

    val fedId = id.getCompetition().getFedId()
    val slug = id.getCompetition().getSlug()

    def getName(): String = {
      return id.getSeasonSlug()
    }

    def getLink() = {
      getUrl("GameController.showClassification", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getUserLink() = {
      getUrl("CompetitionSeasonController.listSeasons", "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("CompetitionSeasonAdminController.edit", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getEnrollLink() = {
      getUrl("CompetitionSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> id.seasonSlug)
    }

    def getEditCompetitionLink() = {
      getUrl("CompetitionAdminController.edit", "slug" -> slug)
    }

    def getDeleteLink() = {
      getUrl("CompetitionSeasonAdminController.delete", "slug" -> slug, "year" -> id.seasonSlug)
    }
  }

  def list(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val competition = competitionService.findBySlug(federation.getId, slug)
    val seasons_ = Option(competition.orElse(Option(new Competition)).get.seasonList)
      .orElse(Some(new ArrayList[CompetitionSeason])).get

    val seasons = seasons_.asScala.map({ season => new CrudSeasonCompetition(season) })

    new ModelAndView("admin/competition/season/list")
      .addObject("competition", competition.orNull)
      .addObject("competitionName", competition.map(l => i.t("Competition %s", l.competitionName)).orNull)
      .addObject("seasons", seasons.asJava)
      .addObject("createUrl", getUrl("CompetitionSeasonAdminController.create", "slug" -> slug))
  }

  def create(
    @RequestParam("slug") competitionSlug: String
  ): ModelAndView = {

    val fedId = federation.getId

    competitionService.findBySlug(fedId, competitionSlug).map {
      l =>
        val competitionSeason = new CompetitionSeason()
        val listLink = getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug)

        new ModelAndView("admin/competition/season/edit_form")
          .addObject("competitionSeason", competitionSeason)
          .addObject("listLink", listLink)
          .addObject("submitUrl", getUrl("CompetitionSeasonAdminController.postCreate"), "slug" -> competitionSlug)
          .addObject("action", i.t("Create Season"))
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def postCreate(
    @RequestParam("slug") competitionSlug: String,
    @Valid competitionSeason: CompetitionSeason,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/competition/season/edit_form")
        .addObject("action", i.t("Create Season"))
        .addObject("listLink", getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug))
    }

    val fedId = federation.getId

    Try(competitionService.createSeason(federation.getId, competitionSlug, competitionSeason.id.seasonSlug,
      competitionSeason.startTime, competitionSeason.endTime)) match {
      case Success(ls) =>
        new ModelAndView("redirect:" + getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug))
      case Failure(e) =>
        if (e.getCause().isInstanceOf[IllegalArgumentException])
          return new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Incorrect parameters"))
            .addObject("errorDescription", i.t("Start time and season slug aren't valid"))

        if (e.getCause().isInstanceOf[InstanceNotFoundException])
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Competition not found"))
            .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))

        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("Unexpected error"))
          .addObject("errorDescription", e.getCause())
    }
  }

  @Autowired
  var conversionService: ConversionService = _

  def edit(
    @RequestParam("slug") competitionSlug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    competitionService.findSeasonByCompetitionSlug(federation.getId, competitionSlug, year).map {
      competitionSeason =>

        val listLink = getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug)

        new ModelAndView("admin/competition/season/edit_form")
          .addObject("competitionSeason", competitionSeason)
          .addObject("listLink", listLink)
          .addObject("submitUrl", getUrl("CompetitionSeasonAdminController.postEdit", "slug" -> competitionSlug, "oldSeasonSlug" -> year))
          .addObject("action", i.t("Edit Season"))
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }

  }

  def postEdit(
    @RequestParam("slug") competitionSlug: String,
    @RequestParam("oldSeasonSlug") oldYear: String,
    @Valid competitionSeason: CompetitionSeason,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/competition/season/edit_form")
        .addObject("action", i.t("Edit Season"))
        .addObject("listLink", getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug))
    }

    val fedId = federation.getId

    competitionService.findSeasonByCompetitionSlug(fedId, competitionSlug, oldYear).map {
      competitionSeason =>
        competitionService.modifySeasonStartTimeBySlug(fedId, competitionSlug, oldYear, competitionSeason.startTime)
        competitionService.modifySeasonEndTimeBySlug(fedId, competitionSlug, oldYear, competitionSeason.endTime)
        competitionService.modifySeasonYearBySlug(fedId, competitionSlug, oldYear, competitionSeason.id.seasonSlug)

        new ModelAndView("redirect:" + getUrl("CompetitionSeasonAdminController.list", "slug" -> competitionSlug))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }
  }

  def delete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    competitionService.findBySlug(federation.getId, slug).map {
      competitionSeason =>
        val listLink = getUrl("CompetitionSeasonAdminController.list", "slug" -> slug)
        val submitUrl = getUrl("CompetitionSeasonAdminController.postDelete", "slug" -> slug, "year" -> year)
        val submitMethod = "POST"

        new ModelAndView("admin/competition/season/delete_confirm")
          .addObject("hiddens", Map("slug" -> slug, "year" -> year).asJava)
          .addObject("listLink", listLink)
          .addObject("seasonName", year)
          .addObject("competitionName", competitionSeason.competitionName)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }
  }

  def postDelete(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String
  ): ModelAndView = {

    var result = competitionService.removeSeasonBySlug(federation.getId, slug, year)

    if (result)
      new ModelAndView("redirect:" + getUrl("CompetitionSeasonAdminController.list", "slug" -> slug))
    else
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
  }

  def enrollTeamInSeason(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    competitionService.findSeasonByCompetitionSlug(fedId, slug, year).map {
      season =>
        val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

        val acceptUrl: String = getUrl("CompetitionSeasonAdminController.list", "slug" -> slug)
        val submitUrl: String = getUrl("CompetitionSeasonAdminController.enrollTeamInSeasonPost", "slug" -> slug, "year" -> year)
        val submitMethod: String = "POST"

        val sv = (slug, year)

        // TODO Modify -> find by federation (now it returns all teams)
        // All teams in federation
        val fullList: Seq[ActionableTeam with TeamEnrollLinks] =
          teamService.findTeamsByFederationId(fedId).map(team =>
            new ActionableTeam(team, hasPermissions) with TeamEnrollLinks {
              val (slug, year) = sv
            })

        // Only registered elements in competition season
        val registeredList: Seq[ActionableCompetitionMember] =
          competitionService.findCompetitionMembersByCompetitionSeasonId(season.id).map(cm => new ActionableCompetitionMember(cm, slug, year))

        new ModelAndView("admin/competition/season/enrollTeam")
          .addObject("competitionSeason", season)
          .addObject("fullList", fullList.asJava)
          .addObject("registeredList", registeredList.asJava)
          .addObject("submitMethod", submitMethod)
          .addObject("submitUrl", submitUrl)
          .addObject("acceptUrl", acceptUrl)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }

  }

  def enrollTeamInSeasonPost(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    @RequestParam("teamId") teamId: Long
  ): ModelAndView = {

    val fedId: Long = federation.getId

    competitionService.findSeasonByCompetitionSlug(fedId, slug, year).map {
      competitionSeason =>
        Try(competitionService.registerTeamInSeason(competitionSeason.id, teamId)) match {
          case Success(cm) =>
            new ModelAndView("redirect:" + getUrl("CompetitionSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))
          case Failure(e) =>
            val cause: Throwable = e.getCause()

            if (cause.isInstanceOf[DuplicateInstanceException])
              return new ModelAndView("redirect:" +
                getUrl("CompetitionSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))

            if ((cause.isInstanceOf[InstanceNotFoundException])) {
              if (e.asInstanceOf[InstanceNotFoundException].getClassName == "CompetitionSeason")
                return new ModelAndView("error/show")
                  .addObject("errorTitle", i.t("Competition season not found"))
                  .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))

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
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }
  }

  def disenrollTeamInSeasonPost(
    @RequestParam("slug") slug: String,
    @RequestParam("year") year: String,
    @RequestParam("teamId") teamId: Long
  ): ModelAndView = {

    val fedId: Long = federation.getId
    competitionService.findSeasonByCompetitionSlug(fedId, slug, year).map {
      competitionSeason =>
        Try(competitionService.removeTeamFromSeason(competitionSeason.id, teamId)) match {
          case Success(cm) =>
            new ModelAndView("redirect:" +
              getUrl("CompetitionSeasonAdminController.enrollTeamInSeason", "slug" -> slug, "year" -> year))
          case Failure(e) =>
            new ModelAndView("error/show")
              .addObject("errorTitle", i.t("Competition member not found"))
              .addObject("errorDescription", i.t("Sorry!, this competition member doesn't exist"))
        }
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition season not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition season doesn't exist"))
    }
  }
}

