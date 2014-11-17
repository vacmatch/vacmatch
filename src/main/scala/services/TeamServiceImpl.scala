package main.scala.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Configurable
import main.scala.models.team.TeamDao
import main.scala.models.team.Team
import org.springframework.beans.factory.annotation.Qualifier

@Service("teamService")
@Transactional
class TeamServiceImpl() extends TeamService {

  var teamDao: TeamDao = _

  def getTeamDao: TeamDao = {
    this.teamDao
  }

  @Autowired
  def setTeamDao(td: TeamDao) = {
    this.teamDao = td
  }

  def createTeam(obj: Team) = {
    teamDao.save(obj)
  }

  def getAllTeams(): Seq[Team] = {
    teamDao.findAll
  }


}
