package com.vac.manager.service.team

import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.competition.LeagueSeasonPK

trait TeamService {

  def find(teamId: Long): Option[Team]

  def findWithTelephonesAndAddress(teamId: Long): Option[Team]

  def findAllTeams(): Seq[Team]

  def findTeamsByFederationId(fedId: Long): Seq[Team]

  def findTeamsByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[Team]

  def findStaffMemberById(staffMemberId: Long): Option[StaffMember]

  def findStaffMemberByTeamIdAndPersonId(teamId: Long, personId: Long): Option[StaffMember]

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember]

  def createTeam(teamName: String, publicName: String, foundationalDate: Calendar,
    address: Address, web: String, telephones: String): Team

  def modifyTeam(teamId: Long, teamName: String, publicName: String,
    foundationalDate: Calendar, address: Address, web: String, telephones: String): Team

  def changeActivation(teamId: Long, newState: Boolean): Option[Team]

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Option[Team]

  def assignPerson(teamId: Long, personId: Long): StaffMember

  def unAssignStaff(teamId: Long, personId: Long): StaffMember

  def getNumberByFederationId(fedId: Long): Long

  def removeTeam(teamId: Long)

}
