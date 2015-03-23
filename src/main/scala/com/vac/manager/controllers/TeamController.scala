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
import com.vac.manager.controllers.actionable.ActionableTeam
import javax.management.InstanceNotFoundException
import scala.collection.JavaConverters._

@Controller
class TeamController()
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var federation: FederationBean = _

  def showTeam(
    @PathVariable("teamId") teamId: java.lang.Long) = {

    teamService.findWithTelephonesAndAddress(teamId).map(team => {

      val listLink: String = getUrl("TeamController.list")

      new ModelAndView("team/showTeam")
        .addObject("team", team)
        .addObject("listLink", listLink)
    }).getOrElse(throw new InstanceNotFoundException("Team not found"))
  }

  def list() = {

    val fedId: Long = federation.getId

    val createLink: String = getUrl("TeamAdminController.create")

    val teamList: Seq[ActionableTeam] = teamService.findAllTeams
      .map(team => new ActionableTeam(team))

    new ModelAndView("team/list")
      .addObject("teamList", teamList.asJava)
      .addObject("createLink", createLink)
  }

}

