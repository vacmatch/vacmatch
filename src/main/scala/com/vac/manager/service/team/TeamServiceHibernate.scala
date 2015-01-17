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

@Service("teamService")
class TeamServiceHibernate() extends TeamService {

  @Autowired
  var teamDao: TeamDao = _

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    teamDao.findTeamsByFederationId(fedId, startIndex, count)
  }

  def findByTeamId(teamId: Long): Team = {
    teamDao.findById(teamId)
  }

  def createTeam(teamName: String, fundationalDate: Calendar): Team = {
    var team: Team = new Team(teamName, fundationalDate)
    //TODO add checks
    teamDao.save(team)
    team
  }

  def modifyTeamName(teamId: Long, newName: String): Team = {
    var team: Team = teamDao.findById(teamId)
    //TODO add checks
    team.setTeamName(newName)
    teamDao.save(team)
    team
  }

  def modifyTeamDate(teamId: Long, newDate: Calendar): Team = {
    var team: Team = teamDao.findById(teamId)
    //TODO add checks
    team.setFundationDate(newDate)
    teamDao.save(team)
    team
  }

  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Team = {
    var team: Team = teamDao.findById(teamId)
    //TODO add checks
    team.setSponsorsList(newSponsors.asJava)
    teamDao.save(team)
    team
  }

  def modifyStaff(teamId: Long, newStaffList: List[Staff]): Team = {
    var team: Team = teamDao.findById(teamId)
    //TODO add checks
    team.setStaffList(newStaffList.asJava)
    teamDao.save(team)
    team
  }

  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Team = {
    var team: Team = teamDao.findById(teamId)
    //TODO add checks
    team.setCompetitionsList(newCompetitionList.asJava)
    teamDao.save(team)
    team
  }

  def getNumberByFederationId(fedId: Long): Long = {
    teamDao.getNumberByFederationId(fedId)
  }

}
