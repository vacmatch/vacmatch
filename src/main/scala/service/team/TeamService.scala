package main.scala.service.team

import main.scala.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import main.scala.model.staff.Staff
import main.scala.model.competition.Competition

trait TeamService {

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]
  
  def findByTeamId(teamId: Long): Team
  
  def createTeam(teamName: String, fundationalDate: Calendar): Team

  def modifyTeamName(teamId: Long, newName: String): Team
  
  def modifyTeamDate(teamId: Long, newDate: Calendar): Team
  
  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team
  
  def modifyStaff(teamId: Long, newStaffList: List[Staff]): Team
  
  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Team

  def getNumberByFederationId(fedId: Long): Long
}



