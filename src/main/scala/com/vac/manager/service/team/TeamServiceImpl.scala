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
import com.vac.manager.model.personal.Address
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.personal.AddressService
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.service.competition.CompetitionService

@Service("teamService")
@Transactional
class TeamServiceImpl extends TeamService {

  @Autowired
  var teamDao: TeamDao = _

  @Autowired
  var addressService: AddressService = _
  
  @Autowired
  var staffService: StaffMemberService = _
  
  @Autowired
  var competitionService: CompetitionService = _

  def find(teamId: Long): Option[Team] = {
    teamDao.findById(teamId)
  }

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    teamDao.findTeamsByFederationId(fedId, startIndex, count)
  }

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team] = {
    teamDao.findTeamsByCompetitionId(compId, fedId)
  }

  @throws[IllegalArgumentException]
  def createTeam(teamName: String, publicName: String, fundationalDate: Calendar,
    address: Address, web: String, telephones: Seq[String]): Team = {

    checkParameters(teamName, publicName, fundationalDate, web, telephones)

    var team: Team = new Team(teamName, publicName, fundationalDate, address, web, telephones.asJava)

    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]
  def modifyTeam(teamId: Long, newName: String, newPublicName: String,
    newDate: Calendar, newAddress: Address, newWeb: String, telephones: Seq[String]): Option[Team] = {

    checkParameters(newName, newPublicName, newDate, newWeb, telephones)
    
    val maybeTeam: Option[Team] = assignAddress(teamId, newAddress)
    
    maybeTeam.map { team =>

      team.teamName = newName
      team.publicTeamName = newPublicName
      team.fundationDate = newDate
      team.teamAddress = newAddress
      team.teamWeb = newWeb
      team.teamTelephones = telephones.asJava
      
      
      teamDao.save(team)
      team
    }
  }

  @throws[IllegalArgumentException]
  private def checkParameters(teamName: String, publicName: String,
      fundationDate: Calendar, web: String, telephones: Seq[String]) {

    if ((teamName == null) || (teamName.isEmpty()))
      throw new IllegalArgumentException(teamName, classOf[String].getName())
    if ((publicName == null) || (publicName.isEmpty))
      throw new IllegalArgumentException(publicName, classOf[Seq[String]].getName())
    if (fundationDate == null)
      throw new IllegalArgumentException(fundationDate, classOf[Calendar].getName())
    if ((web == null) || (web.isEmpty()))
      throw new IllegalArgumentException(web, classOf[String].getName())
    if (telephones == null)
      throw new IllegalArgumentException(telephones, classOf[String].getName())        
  }

  protected def changeTeamDetails(teamId: Long)(callback: (Team) => Any) = {
    var team = teamDao.findById(teamId)

    team.map(callback)
    team.map(teamDao.save(_))
    team
  }
  
  def assignAddress(teamId: Long, newAddress: Address): Option[Team] = {

    val maybeTeam: Option[Team] = find(teamId)

    maybeTeam match {
      case None =>
      case Some(team) => {
        if ((newAddress == null) || (team.teamAddress == newAddress))
          return maybeTeam

        if (team.teamAddress != null)
          addressService.removeAddress(team.teamAddress.addressId)

        val savedAddress: Address = addressService.createAddress(
          newAddress.firstLine, newAddress.secondLine,
          newAddress.postCode, newAddress.locality,
          newAddress.province, newAddress.country
        )

        team.teamAddress = savedAddress
        teamDao.save(team)
      }
    }
    maybeTeam
  }
  
  def changeActivation(teamId: Long, newState: Boolean): Option[Team] = {
    changeTeamDetails(teamId)(_.teamActivated = newState)
  }

  @throws[IllegalArgumentException]("If any element in newSponsors is null")
  def modifyTeamSponsors(teamId: Long, newSponsors: List[String]): Option[Team] = {
    
    newSponsors.map { x =>
      if (x == null)
        throw new IllegalArgumentException(x, x.getClass().getName())
    }
    changeTeamDetails(teamId)(_.setSponsorsList(newSponsors.asJava))
  }

  @throws[IllegalArgumentException]("If any element in newStaffList doesn't exist")
  def modifyStaff(teamId: Long, newStaffList: List[StaffMember]): Option[Team] = {
    
    //Check if all staff exists
    newStaffList.map(st =>
      if (staffService.find(st.staffId) == null)
        throw new IllegalArgumentException(st.staffId,  st.staffId.getClass().getName()))
    changeTeamDetails(teamId)(_.setStaffList(newStaffList.asJava))
  }

  @throws[IllegalArgumentException]("If any element in newCompetitionList doesn't exist")
  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Option[Team] = {

    //Check if all competition exists
    newCompetitionList.map(cp =>
      if (competitionService.find(cp.compId) == null)
        throw new IllegalArgumentException(cp.compId,  cp.compId.getClass().getName()))

    changeTeamDetails(teamId)(_.setCompetitionsList(newCompetitionList.asJava))
  }

  def getNumberByFederationId(fedId: Long): Long = {
    teamDao.getNumberByFederationId(fedId)
  }

}
