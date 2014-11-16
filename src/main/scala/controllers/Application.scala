package main.scala.controllers

import javax.persistence._
import java.util.Calendar
import org.springframework.beans.factory.annotation.Autowired
import main.scala.services.TeamService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import main.scala.models.team.Team
import scala.beans.BeanProperty
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.beans.factory.annotation.Qualifier

@Controller
class Application() {

  var teamService: TeamService = _

  def getTeamService: TeamService = {
    this.teamService
  }

  @Autowired
  def setTeamService(ts: TeamService) ={
    this.teamService = ts
  }

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def index = {
    println("\n\n -------------- ENTRA 1 --------- \n\n")
    null
    "Hello world. Do nothing else"
  }

  @RequestMapping(value = Array("/insert"), method = Array(RequestMethod.GET))
  def insert() = {
    println("\n\n------- ENTRA 2 --------\n\n")
    //Create a new team
    var team: Team = new Team("Carnicería Ángel","Desguaces Herbón",List(""),List(""),Calendar.getInstance())

    //Insert team
    teamService.createTeam(team)

    //Get team
    var insertedTeams: Iterator[Team] = teamService.getAllTeams()

    insertedTeams.foreach{ println }

    var a = "Team: " + team  + " .\nInserted: " +  insertedTeams.toString() + " team_id: "

    var mav: ModelAndView= new ModelAndView("testTeam");
    mav.addObject("team", team);
    mav.addObject("insertedTeams", insertedTeams);
    mav
  }
}
