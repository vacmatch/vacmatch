package com.vac.manager.service.team

import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.competition.Competition

trait TeamService {

  def findByTeamId(teamId: Long): Team

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team]

  def createTeam(teamName: String, fundationalDate: Calendar, address: String): Team

  def modifyTeam(teamId: Long, teamName: String, fundationalDate: Calendar, address: String): Team

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team

  def modifyStaff(teamId: Long, newStaffList: List[Staff]): Team

  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Team

  def getNumberByFederationId(fedId: Long): Long
}
