package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.Competition
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.util.Layout
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import java.lang.Long
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._
import com.vac.manager.controllers.actionable.CrudCompetition
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ModelAttribute
import javax.validation.Valid

@Controller
@Layout("layouts/default_admin")
class CompetitionAdminController extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var federation: FederationBean = _

  def show(
    @RequestParam("slug") slug: String
  ) = {
    val fedId = federation.getId
    competitionService.findBySlug(fedId, slug).map {
      competition =>
        new ModelAndView("admin/competition/show")
          .addObject("competition", competition)
          .addObject("competitionName", i.t("Competition %s", competition.competitionName))
          .addObject("createUrl", getUrl("CompetitionAdminController.create"))
          .addObject("listUrl", getUrl("CompetitionSeasonController.listCompetitions"))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def create() = {
    val fedId = federation.getId

    // Placeholder for values
    val competition = new Competition
    competition.fedId = fedId

    val submitUrl = getUrl("CompetitionAdminController.postCreate")
    val listLink = getUrl("CompetitionSeasonController.listCompetitions")

    new ModelAndView("admin/competition/edit_form")
      .addObject("competition", competition)
      .addObject("listLink", listLink)
      .addObject("hiddens", Map().asJava)
      .addObject("action", i.t("Create competition"))
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", "POST")
  }

  def postCreate(
    @Valid @ModelAttribute("competition") competition: Competition,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/competition/edit_form")
        .addObject("action", i.t("Create competition"))
        .addObject("listLink", getUrl("CompetitionSeasonController.listCompetitions"))
    }

    Try(competitionService.createCompetition(federation.getId, competition.competitionName, competition.slug)) match {
      case Success(l) =>
        new ModelAndView("redirect:" + getUrl("CompetitionSeasonController.listCompetitions"))
      case Failure(e) =>
        result.addError(new FieldError("competition", "slug", competition.slug, true, Array(), Array(), "Duplicate slug"))
        new ModelAndView("admin/competition/edit_form")
          .addObject("action", i.t("Create competition"))
          .addObject("result", result)
          .addObject("competition", competition)
          .addObject("listLink", getUrl("CompetitionSeasonController.listCompetitions"))
    }
  }

  def edit(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    competitionService.findBySlug(federation.getId, slug).map {
      competition =>
        val submitUrl = getUrl("CompetitionAdminController.postEdit", "oldSlug" -> slug)

        val listLink = getUrl("CompetitionSeasonController.listCompetitions")
        // TODO handle notfound competition (Option=None)

        new ModelAndView("admin/competition/edit_form")
          .addObject("competition", competition)
          .addObject("listLink", listLink)
          .addObject("action", i.t("Edit competition"))
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", "POST")
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def postEdit(
    @RequestParam("oldSlug") slug: String,
    @Valid @ModelAttribute("competition") competition: Competition,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/competition/edit_form")
        .addObject("action", i.t("Edit competition"))
        .addObject("listLink", getUrl("CompetitionSeasonController.listCompetitions"))
    }

    val fedId = federation.getId

    val l1 = competitionService.modifyCompetitionName(fedId, slug, competition.competitionName)
    val l2 = competitionService.modifyCompetitionSlug(fedId, slug, competition.slug)

    if (l1.nonEmpty && l2.nonEmpty) {
      new ModelAndView("redirect:" + getUrl("CompetitionSeasonController.listCompetitions"))
    } else {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def delete(
    @RequestParam("slug") slug: String
  ): ModelAndView = {
    val fedId = federation.getId
    val listLink = getUrl("CompetitionSeasonController.listCompetitions")

    competitionService.findBySlug(fedId, slug).map {
      competition =>
        new ModelAndView("admin/competition/delete_confirm")
          .addObject("competition", competition)
          .addObject("listLink", listLink)
          .addObject("action", i.t("Delete competition"))
          .addObject("submitUrl", getUrl("CompetitionAdminController.postDelete"))
          .addObject("hiddens", Map("slug" -> slug).asJava)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

  def postDelete(
    @RequestParam("slug") slug: String
  ): ModelAndView = {

    val result: Boolean = competitionService removeCompetitionBySlug (federation.getId, slug)

    if (!result) {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition can't be removed"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist or has some attached seasons"))
    } else
      new ModelAndView("redirect:" + getUrl("CompetitionSeasonController.listCompetitions"))
  }
}
