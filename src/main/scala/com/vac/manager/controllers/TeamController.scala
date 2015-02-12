package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import com.vac.manager.service.team.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import com.vac.manager.service.personal.AddressService

@Controller
class TeamController() {

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var addressService: AddressService = _
  
  def showTeam() = {
    //TODO get parameters from url
    var teamId: Long = 0
    var team: Team = teamService.findByTeamId(teamId)

    var mav: ModelAndView= new ModelAndView("home");
    mav.addObject("team", team)
    mav
  }

  def insert() = {
    //TODO get parameters from url
    var teamName: String = null
    var publicName: String = null
    var fundationalDate: Calendar = null
    var teamSponsors: List[String] = null
    var teamAddress: String = null
    var web: String = null
    var country: String = ""

    var address: Address = new Address(
      "ADDRESS LINE", "27003", "LOCALITY", "PROVINCE", "COUNTRY")

    var createdTeam: Team = teamService.createTeam(teamName, publicName, fundationalDate, address, web)

    var mav: ModelAndView= new ModelAndView("team/showTeam");
    mav.addObject("team", createdTeam);
    mav
  }

}
