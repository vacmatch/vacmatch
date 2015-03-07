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
import com.vac.manager.model.staff.StaffMemberHistoric
import java.util.ArrayList
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.service.staff.StaffMemberHistoricService
import com.vac.manager.util.FederationBean
import com.vac.manager.controllers.actionable.ActionableStaff

@Controller
class TeamController()
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var staffService: StaffMemberService = _

  @Autowired
  var staffHistoricService: StaffMemberHistoricService = _

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
    @ModelAttribute team: Team,
    @RequestParam("telephones") telephones: String,
    @ModelAttribute address: Address,
    result: BindingResult): ModelAndView = {

    // TODO Check errors
    val telephonesList: Seq[String] = telephones.split(",").map(_.trim).filter(_.nonEmpty)

    val createdTeam: Team = teamService.createTeam(team.teamName,
      team.publicTeamName, Calendar.getInstance, address, team.teamWeb,
      telephonesList)

    new ModelAndView("redirect:" + getUrl("TeamController.showTeam", "teamId" -> createdTeam.teamId))
  }

  def assignStaff(
    @RequestParam teamId: java.lang.Long): ModelAndView = {

    val fedId: Long = federation.getId

    val maybeTeam: Option[Team] = teamService.find(teamId)

    maybeTeam match {
      case None => new ModelAndView("team/assignStaff") //TODO: Handle error
      case Some(team) => {

        // Initialize actual staff list
        val actualStaffHistoricList: java.util.List[StaffMemberHistoric] = team.staffHistoricList

        // Initialize all staff list
        val allStaffList: Seq[ActionableStaff] =
          staffService.findAllByFederationId(fedId).map(s => new ActionableStaff(s))

        // Submit parameters
        val submitUrl = getUrl("TeamController.assignStaffPost")
        val submitMethod = "POST"
        val acceptUrl = getUrl("TeamController.showTeam", "teamId" -> teamId)

        new ModelAndView("team/assignStaff")
          .addObject("hiddens", Map("teamId" -> teamId).asJava.entrySet())
          .addObject("action", "assign")
          .addObject("acceptUrl", acceptUrl)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
          .addObject("teamStaffList", actualStaffHistoricList)
          .addObject("avaliableStaffList", allStaffList.asJava)
      }
    }
  }

  def assignStaffPost(
    @RequestParam staffId: java.lang.Long,
    @RequestParam teamId: java.lang.Long): ModelAndView = {

    var inserted: Either[Exception, Team] = teamService.assignStaff(teamId, staffId)

    // TODO Handle errors

    new ModelAndView("redirect:" + getUrl("TeamController.assignStaff", "teamId" -> teamId))
  }

}

