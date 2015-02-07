package com.vac.manager.service.team

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.competition.Competition
import scala.collection.JavaConverters._
import com.vac.manager.model.staff.StaffMemberDao
import com.vac.manager.model.competition.CompetitionDao
import com.vac.manager.model.personal.Address
import org.springframework.transaction.annotation.Transactional

@Service("teamService")
@Transactional
class TeamServiceImpl extends TeamService {

  @Autowired
  var teamDao: TeamDao = _
  
  @Autowired
  var staffMemberDao: StaffMemberDao = _
  
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
  def createTeam(teamName: String, publicName: String, fundationalDate: Calendar,
      address: Address, web:String): Team = {
    
    var team: Team = new Team(teamName, publicName, fundationalDate, address, web)
    
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
  def modifyTeam(teamId: Long, newName: String, newPublicName: String,
      newDate: Calendar, newAddress: Address, newWeb: String): Team = {
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
    team.publicTeamName = newPublicName
    team.fundationDate = newDate
    team.teamAddress = newAddress
    team.teamWeb = newWeb

    team.setTeamName(newName)
    teamDao.save(team)
    team
  }
      
  def changeActivation(teamId: Long, newState: Boolean): Team = {
    var team: Team = teamDao.findById(teamId)
    team.teamActivated = newState
    
    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]("If newPublicName doesn't exist")
  def modifyPublicName(teamId: Long, newPublicName: String): Team = {
    var team: Team = teamDao.findById(teamId)
    
    if(newPublicName == null){
      throw new IllegalArgumentException("newPublicName cannot be null")
    }
    
    team.publicTeamName = newPublicName
    
    teamDao.save(team)
    team
  }
  
  @throws[IllegalArgumentException]("If newPhones doesn't exist")
  def modifyTelephones(teamId: Long, newPhones: Seq[String]): Team = {
    var team: Team = teamDao.findById(teamId)
    
    if(newPhones == null){
      throw new IllegalArgumentException("newPhones cannot be null")
    }
    
    team.teamTelephones = newPhones.asJava
    
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
  def modifyStaff(teamId: Long, newStaffList: List[StaffMember]): Team = {
    var team: Team = teamDao.findById(teamId)

    //Check if all staff exists
    newStaffList.map(st => 
      if(staffMemberDao.findById(st.staffId) == null)
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
