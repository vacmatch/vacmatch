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

@Controller
class TeamController()
  extends UrlGrabber {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _

  def showTeam(
    @PathVariable("teamId") teamId: java.lang.Long) = {

    var team: Option[Team] = teamService.find(teamId)

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

    // TODO: Check errors

    val telephonesList: Seq[String] = telephones.split(",").map(_.trim).filter(_.nonEmpty)

    val createdTeam: Team = teamService.createTeam(team.teamName,
      team.publicTeamName, Calendar.getInstance, address, team.teamWeb,
      telephonesList)

    new ModelAndView("redirect:" + getUrl("TeamController.showTeam", "teamId" -> createdTeam.teamId))
  }

}
