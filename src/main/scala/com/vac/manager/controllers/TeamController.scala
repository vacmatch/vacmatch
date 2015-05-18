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
import com.vac.manager.model.staff.StaffMember
import javax.servlet.http.HttpServletRequest

@Controller
class TeamController()
    extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var federation: FederationBean = _

  def showTeam(
    @PathVariable("teamId") teamId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    teamService.findWithTelephonesAndAddress(teamId).map(team => {

      // Check user permissions
      val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

      val listLink: String = getUrl("TeamController.list")

      new ModelAndView("team/showTeam")
        .addObject("team", new ActionableTeam(team, hasPermissions))
        .addObject("listLink", listLink)
    }).getOrElse(throw new InstanceNotFoundException("Team not found"))
  }

  def showStaffMembers(
    @PathVariable("teamId") teamId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    // Check user permissions
    val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")
    val listLink: String = getUrl("TeamController.list")

    val staffMemberList: Seq[StaffMember] = teamService.findCurrentStaffMemberListByTeam(teamId)

    teamService.findWithTelephonesAndAddress(teamId).map(team => {

      new ModelAndView("team/showStaffMembers")
        .addObject("staffMemberList", staffMemberList.asJava)
        .addObject("team", new ActionableTeam(team, hasPermissions))
        .addObject("listLink", listLink)
    }).getOrElse(throw new InstanceNotFoundException("Team not found"))
  }

  def list(
    request: HttpServletRequest
  ) = {

    val fedId: Long = federation.getId

    // Check user permissions
    val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    val createLink: String = getUrl("TeamAdminController.create")

    val teamList: Seq[ActionableTeam] = teamService.findAllTeams
      .map(team => new ActionableTeam(team, hasPermissions))

    new ModelAndView("team/list")
      .addObject("hasPermissions", hasPermissions)
      .addObject("teamList", teamList.asJava)
      .addObject("createLink", createLink)
  }

}

