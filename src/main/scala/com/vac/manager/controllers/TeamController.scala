package com.vac.manager.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.team.Team
import com.vac.manager.service.personal.AddressService
import com.vac.manager.service.staff.PersonService
import com.vac.manager.service.team.TeamService
import com.vac.manager.util.FederationBean

import javax.persistence.Entity
import javax.persistence.Table

@Controller
class TeamController()
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  def showTeam(
    @PathVariable("teamId") teamId: java.lang.Long) = {

    var team: Option[Team] = teamService.findWithTelephones(teamId)

    new ModelAndView("team/showTeam")
      .addObject("team", team.get)
  }

}

