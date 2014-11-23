package main.scala.controllers

import org.springframework.stereotype.Controller
import main.scala.service.team.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView
import java.util.Calendar
import main.scala.model.team.Team

@Controller
class TeamController() {

  @Autowired
  var teamService: TeamService = _

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
    var fundationalDate: Calendar = null
    var teamSponsors: List[String] = null
    
    var createdTeam: Team = teamService.createTeam(teamName, fundationalDate)

    var mav: ModelAndView= new ModelAndView("team/showTeam");
    mav.addObject("team", createdTeam);
    mav
  }
  
}
