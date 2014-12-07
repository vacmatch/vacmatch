package main.scala.service.team

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import main.scala.model.team.TeamDao
import main.scala.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import main.scala.model.staff.Staff
import main.scala.model.competition.Competition
import scala.collection.JavaConverters._
import main.scala.model.staff.StaffDao
import main.scala.model.competition.CompetitionDao
import javax.transaction.Transactional

@Service("teamService")
@Transactional
class TeamServiceJPA() extends TeamService {

  @Autowired
  var teamDao: TeamDao = _
  
  @Autowired
  var staffDao: StaffDao = _
  
  @Autowired
  var competitionDao: CompetitionDao = _

  
  def findByTeamId(teamId: Long): Team = {
    teamDao.findById(teamId)
  }

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    teamDao.findTeamsByFederationId(fedId, startIndex, count)
  }

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team] = {
    teamDao.findTeamsByCompetitionId(compId, fedId)
  }
    
  @throws[IllegalArgumentException]("If teamName or fundationalDate doesn't exist")
  def createTeam(teamName: String, fundationalDate: Calendar, address: String): Team = {
    var team: Team = new Team(teamName, fundationalDate, address)
    
    if(teamName == null){
      throw new IllegalArgumentException("teamName cannot be null")
    }
    if((fundationalDate == null) || (fundationalDate.after(Calendar.getInstance()))){
      throw new IllegalArgumentException("fundationalDate cannot be null")
    }
    
    if(address == null){
      throw new IllegalArgumentException("address cannot be null")
    }
    
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If newName, newDate or newAddress doesn't exist")
  def modifyTeam(teamId: Long, newName: String, newDate: Calendar, newAddress: String): Team = {
    var team: Team = teamDao.findById(teamId)
    
    if(newName == null){
      throw new IllegalArgumentException("newName cannot be null")
    }
    
    if((newDate == null) || (newDate.compareTo(Calendar.getInstance()) != 1)){
     throw new IllegalArgumentException("newDate cannot be null")
    }
    
    if(newAddress == null){
      throw new IllegalArgumentException("newAddress cannot be null")
    }
    
    team.teamName = newName
    team.fundationDate = newDate
    team.teamAddress = newAddress

    team.setTeamName(newName)
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If any element in newSponsors is null")
  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team = {
    var team: Team = teamDao.findById(teamId)
   
    newSponsors.map(x => 
      if(x==null)
      throw new IllegalArgumentException("Illegal null element in newSponsors")
    )

    team.setSponsorsList(newSponsors.asJava)
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If any element in newStaffList doesn't exist")
  def modifyStaff(teamId: Long, newStaffList: List[Staff]): Team = {
    var team: Team = teamDao.findById(teamId)

    //Check if all staff exists
    newStaffList.map(st => 
      if(staffDao.findById(st.staffId) == null)
    	  throw new IllegalArgumentException("staffId " + st.staffId + " cannot be null"))
    	  
    team.setStaffList(newStaffList.asJava)
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If any element in newCompetitionList doesn't exist")
  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Team = {
    var team: Team = teamDao.findById(teamId)

    //Check if all competition exists
    newCompetitionList.map(cp => 
      if(competitionDao.findById(cp.compId) == null)
    	  throw new IllegalArgumentException("compId " + cp.compId + " cannot be null"))
    
    team.setCompetitionsList(newCompetitionList.asJava)
    teamDao.save(team)
    team
  }

  def getNumberByFederationId(fedId: Long): Long = {
    teamDao.getNumberByFederationId(fedId)
  }

}
