package com.vac.manager.controllers

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.Competition
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.util.FederationBean
import com.vacmatch.util.i18n.I18n
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
class CompetitionController {

  @Autowired
  var i: I18n = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var federation: FederationBean = _

  class ActionableCompetition(base: Competition) extends Competition with UrlGrabber {

    fedId = base.fedId
    competitionName = base.competitionName
    slug = base.slug

    def getSeasonsLink() = {
      getUrl("CompetitionSeasonController.listSeasons", "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("CompetitionAdminController.edit", "slug" -> slug)
    }

    def getDeleteLink() = {
      getUrl("CompetitionAdminController.delete", "slug" -> slug)
    }
  }

  def list() = {
    val fedId = federation.getId()

    var competitions: Seq[Competition] = competitionService
      .findAllByFederation(fedId) //.map({ season => season.id.competition })
      .map({ competition => new ActionableCompetition(competition) })

    val mav: ModelAndView = new ModelAndView("competition/list")
    mav.addObject("competitions", competitions.asJava)
    mav.addObject("d", fedId)
    mav
  }

  def show(@PathVariable("slug") slug: String) = {
    val fedId = federation.getId()
    competitionService.findBySlug(fedId, slug).map {
      competition =>
        new ModelAndView("competition/show")
          .addObject("competition", competition)
          .addObject("competitionName", i.t("Competition %s", competition.competitionName))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Competition not found"))
        .addObject("errorDescription", i.t("Sorry!, this competition doesn't exist"))
    }
  }

}
