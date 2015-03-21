package com.vac.manager.service.team

import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.StaffMember

trait TeamService {

  def find(teamId: Long): Option[Team]

  def findWithTelephones(teamId: Long): Option[Team]

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team]

  def findStaffMemberById(staffMemberId: Long): Option[StaffMember]

  def findStaffMemberByTeamIdAndPersonId(teamId: Long, personId: Long): Option[StaffMember]

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember]

  def createTeam(teamName: String, publicName: String, foundationalDate: Calendar,
    address: Address, web: String, telephones: Seq[String]): Team

  def modifyTeam(teamId: Long, teamName: String, publicName: String,
    foundationalDate: Calendar, address: Address, web: String, telephones: Seq[String]): Option[Team]

  def changeActivation(teamId: Long, newState: Boolean): Option[Team]

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Option[Team]

  def assignPerson(teamId: Long, personId: Long): StaffMember

  def unAssignStaff(teamId: Long, personId: Long): StaffMember

  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Option[Team]

  def getNumberByFederationId(fedId: Long): Long

}
