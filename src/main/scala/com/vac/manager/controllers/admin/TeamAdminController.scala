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
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import java.util.GregorianCalendar
import java.util.Date
import scala.util.Try
import com.vac.manager.controllers.actionable.ActionableTeam
import javax.servlet.http.HttpServletRequest
import com.vacmatch.util.i18n.I18n
import javax.validation.Valid
import scala.util.Success
import scala.util.Failure
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException

@Controller
class TeamAdminController
    extends UrlGrabber {

  @Autowired
  var i: I18n = _

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personService: PersonService = _

  @Autowired
  var federation: FederationBean = _

  def create(): ModelAndView = {

    val fedId: Long = federation.getId

    // Create receivers
    val receiverTeam = new Team()
    val receiverAddress = new Address()
    // Submit parameters
    val submitUrl = getUrl("TeamAdminController.createPost")
    val submitMethod = "POST"
    val listLink: String = getUrl("TeamController.list")

    new ModelAndView("admin/team/edit")
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
      .addObject("action", "Create")
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
      .addObject("listLink", listLink)
      .addObject("address", receiverAddress)
      .addObject("team", receiverTeam)
  }

  def createPost(
    @ModelAttribute("address") address: Address,
    @Valid @ModelAttribute("team") team: Team,
    result: BindingResult,
    request: HttpServletRequest
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/team/edit")
        .addObject("action", "Create")
        .addObject("listLink", getUrl("TeamController.list"))
    }

    Try(teamService.createTeam(
      team.teamName,
      team.publicTeamName, team.foundationDate, address, team.teamWeb,
      team.teamTelephones
    )) match {
      case Success(createdTeam) =>
        new ModelAndView("redirect:" + getUrl("TeamController.showTeam", "teamId" -> createdTeam.teamId))
      case Failure(e) =>
        val cause: Throwable = e.getCause()

        if (cause.isInstanceOf[IllegalArgumentException]) {
          val referrer: String = request.getHeader("Referer");

          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Incorrect team name"))
            .addObject("errorDescription", i.t("You must specify a team name and a public team name for a new team"))
            .addObject("backLink", referrer)
            .addObject("backText", i.t("Back to create team"))
        } else
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Unexpected error"))
            .addObject("errorDescription", cause)
    }
  }

  def edit(
    @RequestParam("teamId") teamId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    teamService.findWithTelephonesAndAddress(teamId).map {
      team =>
        // Check user permissions
        val hasPermissions: Boolean = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

        // Submit parameters
        val submitUrl: String = getUrl("TeamAdminController.editPost", "teamId" -> teamId)
        val submitMethod: String = "POST"
        val listLink: String = getUrl("TeamController.list")

        new ModelAndView("admin/team/edit")
          .addObject("action", "Edit")
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
          .addObject("listLink", listLink)
          .addObject("address", team.teamAddress)
          .addObject("team", new ActionableTeam(team, hasPermissions))
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Team not found"))
        .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
    }

  }

  def editPost(
    @RequestParam("teamId") teamId: java.lang.Long,
    @ModelAttribute("address") address: Address,
    @Valid @ModelAttribute("team") team: Team,
    result: BindingResult
  ): ModelAndView = {

    if (result.hasErrors()) {
      return new ModelAndView("admin/team/edit")
        .addObject("action", "Edit")
        .addObject("listLink", getUrl("TeamController.list"))
    }

    Try(teamService.modifyTeam(
      teamId,
      team.teamName,
      team.publicTeamName,
      team.foundationDate,
      address,
      team.teamWeb,
      team.teamTelephones
    )) match {
      case Success(editedTeam) =>
        new ModelAndView("redirect:" + getUrl("TeamController.showTeam", "teamId" -> editedTeam.teamId))

      case Failure(e) =>
        val cause: Throwable = e.getCause()
        if (cause.isInstanceOf[IllegalArgumentException])
          return new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Incorrect team name"))
            .addObject("errorDescription", i.t("You must specify a team name and a public team name for a new team"))

        if (cause.isInstanceOf[InstanceNotFoundException])
          return new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Team not found"))
            .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))

        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("Unexpected error"))
          .addObject("errorDescription", cause)
    }

  }

  def assignStaffMember(
    @RequestParam("teamId") teamId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    teamService.find(teamId).map { team =>

      // Check user permissions
      val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

      // Initialize current person list
      val actualStaffMemberList: Seq[ActionableStaffMember] =
        teamService.findCurrentStaffMemberListByTeam(teamId).map(s => new ActionableStaffMember(s))

      // Initialize all person list
      val allPersonList: Seq[ActionablePerson] =
        personService.findAllByFederationId(fedId).map(s => new ActionablePerson(s, userCanEdit))

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
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Team not found"))
        .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
    }
  }

  def assignStaffMemberPost(
    @RequestParam("personId") personId: java.lang.Long,
    @RequestParam("teamId") teamId: java.lang.Long
  ): ModelAndView = {

    Try(teamService.assignPerson(teamId, personId)) match {
      case Success(staffMember) =>
        new ModelAndView("redirect:" + getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId))
      case Failure(e) =>
        val cause: Throwable = e.getCause()

        if (cause.isInstanceOf[DuplicateInstanceException])
          return new ModelAndView("redirect:" + getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId))

        if (cause.isInstanceOf[IllegalArgumentException])
          return new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Incorrect team name"))
            .addObject("errorDescription", i.t("You must specify a team name and a public team name for a new team"))

        if (cause.isInstanceOf[InstanceNotFoundException]) {
          if (cause.asInstanceOf[InstanceNotFoundException].getClassName == "Person")
            return new ModelAndView("error/show")
              .addObject("errorTitle", i.t("Person not found"))
              .addObject("errorDescription", i.t("Sorry!, this person doesn't exist"))

          if (cause.asInstanceOf[InstanceNotFoundException].getClassName == "Team")
            return new ModelAndView("error/show")
              .addObject("errorTitle", i.t("Team not found"))
              .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
        }

        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("Unexpected error"))
          .addObject("errorDescription", cause)
    }
  }

  def unAssignStaffMemberPost(
    @RequestParam("personId") personId: java.lang.Long,
    @RequestParam("teamId") teamId: java.lang.Long
  ): ModelAndView = {

    Try(teamService.unAssignStaff(teamId, personId)) match {
      case Success(staffMember) =>
        new ModelAndView("redirect:" + getUrl("TeamAdminController.assignStaffMember", "teamId" -> teamId))
      case Failure(e) =>
        val cause: Throwable = e.getCause()

        if (cause.isInstanceOf[InstanceNotFoundException])
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Staff member not found"))
            .addObject("errorDescription", i.t("Sorry!, this staff member doesn't exist"))
        else
          new ModelAndView("error/show")
            .addObject("errorTitle", i.t("Unexpected error"))
            .addObject("errorDescription", cause)
    }
  }

  def delete(
    @RequestParam("teamId") teamId: java.lang.Long
  ): ModelAndView = {

    val fedId: Long = federation.getId
    val submitMethod: String = "POST"
    val submitUrl: String = getUrl("TeamAdminController.deletePost", "teamId" -> teamId)

    teamService.find(teamId).map {
      team =>
        new ModelAndView("admin/team/delete_confirm")
          .addObject("team", new ActionableTeam(team, true))
          .addObject("submitMethod", submitMethod)
          .addObject("submitUrl", submitUrl)
    }.getOrElse {
      new ModelAndView("error/show")
        .addObject("errorTitle", i.t("Team not found"))
        .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
    }

  }

  def deletePost(
    @RequestParam("teamId") teamId: java.lang.Long
  ): ModelAndView = {

    Try(teamService.removeTeam(teamId)) match {
      case Success(_) =>
        new ModelAndView("redirect:" + getUrl("TeamController.list"))
      case Failure(e) =>
        new ModelAndView("error/show")
          .addObject("errorTitle", i.t("Team not found"))
          .addObject("errorDescription", i.t("Sorry!, this team doesn't exist"))
    }
  }

}

