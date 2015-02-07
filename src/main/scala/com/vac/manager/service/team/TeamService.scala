package com.vac.manager.service.team

import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.personal.Address

trait TeamService {

  def findByTeamId(teamId: Long): Team

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team]

  def createTeam(teamName: String, publicName: String, fundationalDate: Calendar,
      address: Address, web: String): Team

  def modifyTeam(teamId: Long, teamName: String, publicName: String,
      fundationalDate: Calendar, address: Address, web: String): Team

  def changeActivation(teamId: Long, newState: Boolean): Team

  def modifyPublicName(teamId: Long, newPublicName: String): Team

  def modifyTelephones(teamId: Long, newPhones: Seq[String]): Team

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team

  def modifyStaff(teamId: Long, newStaffList: List[StaffMember]): Team

  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Team

  def getNumberByFederationId(fedId: Long): Long

}
