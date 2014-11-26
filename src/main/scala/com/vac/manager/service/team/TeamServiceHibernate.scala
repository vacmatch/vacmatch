package com.vac.manager.service.team

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.competition.Competition
import scala.collection.JavaConverters._
import main.scala.model.staff.StaffDao
import main.scala.model.competition.CompetitionDao

@Service("teamService")
class TeamServiceHibernate() extends TeamService {

  @Autowired
  var teamDao: TeamDao = _
  
  @Autowired
  var staffDao: StaffDao = _
  
  @Autowired
  var competitionDao: CompetitionDao = _

  
  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    teamDao.findTeamsByFederationId(fedId, startIndex, count)
  }

  def findByTeamId(teamId: Long): Team = {
    teamDao.findById(teamId)
  }

  @throws[IllegalArgumentException]("If teamName or fundationalDate doesn't exist")
  def createTeam(teamName: String, fundationalDate: Calendar): Team = {
    var team: Team = new Team(teamName, fundationalDate)
    
    if(teamName == null){
      throw new IllegalArgumentException("teamName cannot be null")
    }
    if((fundationalDate == null) || (fundationalDate.after(Calendar.getInstance()))){
      throw new IllegalArgumentException("fundationalDate cannot be null")
    }
    
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If newName doesn't exist")
  def modifyTeamName(teamId: Long, newName: String): Team = {
    var team: Team = teamDao.findById(teamId)

    if(newName == null){
      throw new IllegalArgumentException("newName cannot be null")
    }
    
    team.setTeamName(newName)
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If newDate doesn't exist")
  def modifyTeamDate(teamId: Long, newDate: Calendar): Team = {
    var team: Team = teamDao.findById(teamId)

    if((newDate == null) || (newDate.compareTo(Calendar.getInstance()) != 1)){
      throw new IllegalArgumentException("newDate cannot be null")
    }
    
    team.setFundationDate(newDate)
    teamDao.save(team)
    team
  }

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team = {
    var team: Team = teamDao.findById(teamId)

    team.setSponsorsList(newSponsors.asJava)
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If any element in newStaffList doesn't exist")
  def modifyStaff(teamId: Long, newStaffList: List[Staff]): Team = {
    var team: Team = teamDao.findById(teamId)

    //Check if all staff exists
    newStaffList.foreach(st => 
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
    newCompetitionList.foreach(cp => 
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
