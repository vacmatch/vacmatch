package com.vac.manager.controllers

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.competition.League
import com.vac.manager.service.competition.LeagueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{ PathVariable, RequestParam }
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
class LeagueController {

  @Autowired
  var leagueService: LeagueService = _

  class ActionableLeague(base: League) extends League with UrlGrabber {

    fedId = base.fedId
    leagueName = base.leagueName
    slug = base.slug

    def getSeasonsLink() = {
      getUrl("LeagueSeasonController.listSeasons", "fedId" -> fedId, "slug" -> slug)
    }

    def getEditLink() = {
      getUrl("LeagueAdminController.edit", "slug" -> slug, "fedId" -> fedId)
    }

    def getDeleteLink() = {
      getUrl("LeagueAdminController.delete", "slug" -> slug, "fedId" -> fedId)
    }
  }

  def list(@RequestParam("fedId") fedId: Int) = {
    var leagues: Seq[League] = leagueService
      .findAllByFederation(fedId) //.map({ season => season.id.league })
      .map({ league => new ActionableLeague(league) })

    val mav: ModelAndView = new ModelAndView("league/list")
    mav.addObject("leagues", leagues.asJava)
    mav.addObject("d", fedId)
    mav
  }

  def show(
    @RequestParam("fedId") fedId: Int,
    @PathVariable("slug") slug: String
  ) = {

    val league = leagueService.findBySlug(fedId, slug)

    val mav = new ModelAndView("league/show")
    mav.addObject("league", league.get)
  }

}
