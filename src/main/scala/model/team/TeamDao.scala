package main.scala.model.team

import main.scala.model.generic.GenericDao
import main.scala.model.staff.Staff
import main.scala.model.competition.Competition

trait TeamDao extends GenericDao[Team, java.lang.Long]{

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findByTeamName(name: String): Team
  
  def getTeamSponsors(teamId: Long): List[String]

  def getTeamStaff(teamId: Long): List[Staff]

  def getTeamCompetitions(teamId: Long): List[Competition]

  def hasCompetitions(id: Long): Boolean

  def getNumberByFederationId(fedId: Long): Long
  
}