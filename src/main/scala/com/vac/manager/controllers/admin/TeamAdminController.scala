package com.vac.manager.controllers.admin

import com.vac.manager.model.staff.StaffMember
import org.springframework.validation.BindingResult
import com.vac.manager.model.personal.Address
import com.vac.manager.controllers.actionable.ActionableStaffMember
import com.vac.manager.model.team.Team
import com.vac.manager.controllers.actionable.ActionablePerson
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ModelAttribute
import java.util.Calendar
import com.vac.manager.controllers.utils.UrlGrabber
import scala.collection.JavaConverters._
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.team.TeamService
import com.vac.manager.service.personal.AddressService
import com.vac.manager.service.staff.PersonService
import com.vac.manager.util.FederationBean
import org.springframework.stereotype.Controller

@Controller
class TeamAdminController
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personService: PersonService = _

  @Autowired
  var federation: FederationBean = _

  def create(): ModelAndView = {

    // Create receivers
    val receiverTeam = new Team()
    val receiverAddress = new Address()
    // Submit parameters
    val submitUrl = getUrl("TeamAdminController.createPost")
    val submitMethod = "POST"

    new ModelAndView("admin/team/edit")
      .addObject("hiddens", List().asJava)
      .addObject("action", "Create")
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
      .addObject("team", receiverTeam)
      .addObject("address", receiverAddress)
  }

  def createPost(
    @ModelAttribute("team") team: Team,
    @RequestParam("telephones") telephones: String,
    @ModelAttribute("address") address: Address,
    result: BindingResult): ModelAndView = {

    // TODO Check errors
    val telephonesList: Seq[String] = telephones.split(",").map(_.trim).filter(_.nonEmpty)

    val createdTeam: Team = teamService.createTeam(team.teamName,
      team.publicTeamName, Calendar.getInstance, address, team.teamWeb,
      telephonesList)

    new ModelAndView("redirect:" + getUrl("TeamController.showTeam", "teamId" -> createdTeam.teamId))
  }

  def assignStaffMember(
    @RequestParam("teamId") teamId: java.lang.Long): ModelAndView = {

    val fedId: Long = federation.getId

    teamService.find(teamId).map { team =>

      // Initialize current person list
      val actualStaffMemberList: Seq[ActionableStaffMember] =
        teamService.findCurrentStaffMemberListByTeam(teamId).map(s => new ActionableStaffMember(s))

      // Initialize all person list
      val allPersonList: Seq[ActionablePerson] =
        personService.findAllByFederationId(fedId).map(s => new ActionablePerson(s))

      // Submit parameters
      val submitMethod = "POST"
      val acceptUrl = getUrl("TeamController.showTeam", "teamId" -> teamId)

      new ModelAndView("admin/team/assignStaffMember")
        .addObject("hiddens", Map("teamId" -> teamId).asJava.entrySet())
        .addObject("action", "assign")
        .addObject("acceptUrl", acceptUrl)
        .addObject("submitMethod", submitMethod)
        .addObject("teamStaffMemberList", actualStaffMemberList.asJava)
        .addObject("avaliablePersonList", allPersonList.asJava)
    }.getOrElse(throw new RuntimeException("Team not found"))
  }

  def assignStaffMemberPost(
    @RequestParam("personId") personId: java.lang.Long,
    @RequestParam("teamId") teamId: java.lang.Long): ModelAndView = {

    val staffMember: StaffMember = teamService.assignPerson(teamId, personId)

    // TODO Handle errors

    new ModelAndView("redirect:" + getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId))
  }

  def unAssignStaffMemberPost(
    @RequestParam("personId") personId: java.lang.Long,
    @RequestParam("teamId") teamId: java.lang.Long): ModelAndView = {

    val staffMember: StaffMember = teamService.unAssignStaff(teamId, personId)

    // TODO Handle errors

    new ModelAndView("redirect:" + getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId))
  }

}