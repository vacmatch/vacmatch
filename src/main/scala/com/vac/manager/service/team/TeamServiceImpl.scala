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
import com.vac.manager.model.staff.StaffMemberHistoric
import com.vac.manager.service.staff.StaffMemberHistoricService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import java.util.Arrays.ArrayList
import java.util.ArrayList

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

  @Transactional
  def findWithTelephones(teamId: Long): Option[Team] = {
    val optionTeam: Option[Team] = teamDao.findById(teamId)

    // Force eager load
    optionTeam.map(_.teamTelephones.size())

    optionTeam
  }

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    teamDao.findTeamsByFederationId(fedId, startIndex, count)
  }

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team] = {
    teamDao.findTeamsByCompetitionId(compId, fedId)
  }

  @throws[IllegalArgumentException]
  def createTeam(teamName: String, publicName: String, foundationalDate: Calendar,
    address: Address, web: String, telephones: Seq[String]): Team = {

    checkParameters(teamName, publicName, foundationalDate, web, telephones)

    var team: Team = new Team(teamName, publicName, foundationalDate, address, web, telephones.asJava)

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
      team.foundationDate = newDate
      team.teamAddress = newAddress
      team.teamWeb = newWeb
      team.teamTelephones = telephones.asJava

      teamDao.save(team)
      team
    }
  }

  @throws[IllegalArgumentException]
  private def checkParameters(teamName: String, publicName: String,
    foundationDate: Calendar, web: String, telephones: Seq[String]) {

    val checkAgainstNull = List((teamName, classOf[String]), (publicName, classOf[String]),
      (foundationDate, classOf[Calendar]), (web, classOf[String]), (telephones, classOf[String]))
    val checkAgainstEmpty = List((teamName, classOf[String]), (publicName,
      classOf[String]), (web, classOf[String]))

    checkAgainstNull.map {
      case (elt, cls) =>
        if (Option(elt).isEmpty)
          throw new IllegalArgumentException(elt, cls.getName())
    }
    checkAgainstEmpty.map {
      case (elt, cls) =>
        if (Option(elt).exists(_.trim == ""))
          throw new IllegalArgumentException(elt, cls.getName())
    }
  }

  protected def changeTeamDetails(teamId: Long)(callback: (Team) => Any) = {
    var team = teamDao.findById(teamId)

    team.map(callback)
    team.map(teamDao.save(_))
    team
  }

  def assignAddress(teamId: Long, newAddress: Address): Option[Team] = {

  private def assignAddress(teamId: Long, newAddress: Address): Option[Team] = {

    val maybeTeam: Option[Team] = find(teamId)

    maybeTeam.map { team =>
      if (!Option(newAddress).exists(_ == team.teamAddress)) {
        Option(team.teamAddress).map(address => addressService.removeAddress(address.addressId))

        val savedAddress: Address = addressService.createAddress(
          newAddress.firstLine, newAddress.secondLine,
          newAddress.postCode, newAddress.locality,
          newAddress.province, newAddress.country)

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
      if (Option(x).isEmpty)
        throw new IllegalArgumentException(x, x.getClass().getName())
    }
    changeTeamDetails(teamId)(_.setSponsorsList(newSponsors.asJava))
  }

  @throws[InstanceNotFoundException]
  def assignStaff(teamId: Long, staffId: Long): Either[Exception, Team] = {

    val maybeStaff: Option[StaffMember] = staffService.find(staffId)

    if (maybeStaff.isEmpty)
      return Left(new InstanceNotFoundException(maybeStaff, classOf[String].getName()))

    val maybeTeam: Option[Team] = find(teamId)

    maybeTeam match {
      case None => Left(new InstanceNotFoundException(maybeTeam, classOf[String].getName()))
      case Some(team) => {

        val eitherStaffHistoric: Either[Exception, StaffMemberHistoric] =
          staffHistoricService.create(maybeStaff.get, team)

        eitherStaffHistoric match {
          case Left(e) => Left(e)
          case Right(staffHistoric) => {
            team.staffHistoricList.add(staffHistoric)
            teamDao.save(team)

            Right(team)
          }
        }
      }
    }
  }

  @throws[IllegalArgumentException]("If any element in newCompetitionList doesn't exist")
  def modifyCompetitions(teamId: Long, newCompetitionList: List[Competition]): Option[Team] = {

    //Check if all competition exists
    newCompetitionList.map(cp =>
      if (competitionService.find(cp.compId).isEmpty)
        throw new IllegalArgumentException(cp.compId, cp.compId.getClass().getName()))

    changeTeamDetails(teamId)(_.setCompetitionsList(newCompetitionList.asJava))
  }

  def getNumberByFederationId(fedId: Long): Long = {
    teamDao.getNumberByFederationId(fedId)
  }

}
