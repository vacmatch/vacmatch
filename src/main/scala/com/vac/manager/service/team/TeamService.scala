package com.vac.manager.service.team

import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.personal.Address

trait TeamService {

  def find(teamId: Long): Option[Team]

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team]

  def createTeam(teamName: String, publicName: String, fundationalDate: Calendar,
      address: Address, web: String, telephones: Seq[String]): Team

  def modifyTeam(teamId: Long, teamName: String, publicName: String,
      fundationalDate: Calendar, address: Address, web: String, telephones: Seq[String]): Option[Team]

  def assignAddress(teamId: Long, newAddress: Address): Option[Team]

  def changeActivation(teamId: Long, newState: Boolean): Option[Team]

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Option[Team]

  def modifyStaff(teamId: Long, newStaffList: List[StaffMember]): Option[Team]

  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Option[Team]

  def getNumberByFederationId(fedId: Long): Long

}
