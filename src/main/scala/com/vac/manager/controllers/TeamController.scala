package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import com.vac.manager.service.team.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import com.vac.manager.service.personal.AddressService
import com.vac.manager.controllers.utils.UrlGrabber
import scala.collection.JavaConverters._
import org.springframework.web.bind.annotation.PathVariable
import scala.beans.BeanProperty
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ModelAttribute
import com.vac.manager.model.staff.StaffMember
import java.util.ArrayList
import com.vac.manager.service.staff.PersonService
import com.vac.manager.model.staff.Person
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.util.FederationBean
import com.vac.manager.controllers.actionable.ActionablePerson

@Controller
class TeamController()
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personService: PersonService = _

  @Autowired
  var staffMemberService: StaffMemberService = _

  @Autowired
  var federation: FederationBean = _

  def showTeam(
    @PathVariable("teamId") teamId: java.lang.Long) = {

    var team: Option[Team] = teamService.findWithTelephones(teamId)

    new ModelAndView("team/showTeam")
      .addObject("team", team.get)
  }

  def create(): ModelAndView = {

    // Create receivers
    val receiverTeam = new Team()
    val receiverAddress = new Address()
    // Submit parameters
    val submitUrl = getUrl("TeamController.createPost")
    val submitMethod = "POST"

    new ModelAndView("team/edit")
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

    val maybeTeam: Option[Team] = teamService.find(teamId)

    maybeTeam match {
      case None => throw new Exception() // TODO: Handle error
      case Some(team) => {

        // Initialize current person list
        val actualStaffMemberList: Seq[StaffMember] =
          staffMemberService.findCurrentStaffMemberListByTeam(teamId)

        // Initialize all person list
        val allPersonList: Seq[ActionablePerson] =
          personService.findAllByFederationId(fedId).map(s => new ActionablePerson(s))

        // Submit parameters
        val submitUrl = getUrl("TeamController.assignStaffMemberPost")
        val submitMethod = "POST"
        val acceptUrl = getUrl("TeamController.showTeam", "teamId" -> teamId)

        new ModelAndView("team/assignStaffMember")
          .addObject("hiddens", Map("teamId" -> teamId).asJava.entrySet())
          .addObject("action", "assign")
          .addObject("acceptUrl", acceptUrl)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
          .addObject("teamStaffMemberList", actualStaffMemberList.asJava)
          .addObject("avaliablePersonList", allPersonList.asJava)
      }
    }
  }

  def assignStaffMemberPost(
    @RequestParam("personId") personId: java.lang.Long,
    @RequestParam("teamId") teamId: java.lang.Long): ModelAndView = {

    var inserted: Either[Exception, Team] = teamService.assignPerson(teamId, personId)

    // TODO Handle errors

    new ModelAndView("redirect:" + getUrl("TeamController.assignStaffMember", "teamId" -> teamId))
  }

}

