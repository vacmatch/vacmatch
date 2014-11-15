package main.scala

import main.scala.services.TeamService
import org.springframework.context.support.ClassPathXmlApplicationContext
import main.scala.models.team.Team

class Main {

  def main(args: List[String]) = {
    new Main().start()
  }
  
  def start() = {
    
    // open/read the application context file
    var ctx: ClassPathXmlApplicationContext = new ClassPathXmlApplicationContext("application.xml")
    
    var teamServ: TeamService = ctx.getBean("TeamService").asInstanceOf[TeamService]
    var testTeam: Team = new Team()
    
    teamServ.createTeam(testTeam)
    
    println(testTeam)
  }


}
