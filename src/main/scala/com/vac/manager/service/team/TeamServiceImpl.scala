package com.vac.manager.service.team

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.team.Team
import javax.persistence.Entity
import javax.persistence.Table
import java.util.Calendar
import com.vac.manager.model.staff.Person
import scala.collection.JavaConverters._
import com.vac.manager.model.personal.Address
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.personal.AddressService
import com.vac.manager.service.staff.PersonService
import com.vac.manager.model.staff.StaffMember
import java.util.Arrays.ArrayList
import java.util.ArrayList
import com.vac.manager.model.staff.StaffMemberDao
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.competition.CompetitionSeasonPK

@Service("teamService")
@Transactional
class TeamServiceImpl extends TeamService {

  @Autowired
  var teamDao: TeamDao = _

  @Autowired
  var staffMemberDao: StaffMemberDao = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var personService: PersonService = _

  def find(teamId: Long): Option[Team] = {
    teamDao.findById(teamId)
  }

  @Transactional
  def findWithTelephonesAndAddress(teamId: Long): Option[Team] = {
    val optionTeam: Option[Team] = teamDao.findById(teamId)

    // Force eager load
    optionTeam.map(_.teamTelephones.toString)
    optionTeam.map(_.teamAddress.toString)

    optionTeam
  }

  def findTeamsByFederationId(fedId: Long): Seq[Team] = {
    teamDao.findTeamsByFederationId(fedId)
  }

  def findAllTeams(): Seq[Team] = {
    teamDao.findAll
  }

  def findTeamsByCompetitionSeasonId(competitionSeasonId: CompetitionSeasonPK): Seq[Team] = {
    teamDao.findTeamsByCompetitionSeasonId(competitionSeasonId)
  }

  def findStaffMemberById(staffMemberId: Long): Option[StaffMember] = {
    staffMemberDao.findById(staffMemberId)
  }

  def findStaffMemberByTeamIdAndPersonId(teamId: Long, personId: Long): Option[StaffMember] = {
    staffMemberDao.find(teamId, personId)
  }

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember] = {
    staffMemberDao.findCurrentStaffMemberListByTeam(teamId)
  }

  @throws[IllegalArgumentException]
  def createTeam(teamName: String, publicName: String, foundationalDate: Calendar,
    address: Address, web: String, telephones: String): Team = {

    checkParameters(teamName, publicName, foundationalDate, web, telephones)

    var team: Team = new Team(teamName, publicName, foundationalDate, address, web, telephones)

    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]
  @throws[InstanceNotFoundException]("If team doesn't exist")
  def modifyTeam(teamId: Long, newName: String, newPublicName: String,
    newDate: Calendar, newAddress: Address, newWeb: String, telephones: String): Team = {

    checkParameters(newName, newPublicName, newDate, newWeb, telephones)

    val team: Team = assignAddress(teamId, newAddress)

    team.teamName = newName
    team.publicTeamName = newPublicName
    team.foundationDate = newDate
    team.teamAddress = newAddress
    team.teamWeb = newWeb
    team.teamTelephones = telephones

    teamDao.save(team)
    team
  }

  @throws[IllegalArgumentException]
  private def checkParameters(teamName: String, publicName: String,
    foundationDate: Calendar, web: String, telephones: String) {

    val checkAgainstNull = List((teamName, classOf[String]), (publicName, classOf[String]))
    val checkAgainstEmpty = List((teamName, classOf[String]), (publicName, classOf[String]))

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

  @throws[InstanceNotFoundException]("If team doesn't exist")
  private def assignAddress(teamId: Long, newAddress: Address): Team = {

    find(teamId).map {
      team =>
        {
          if (!Option(newAddress).exists(_ == team.teamAddress)) {
            Option(team.teamAddress).map(address => addressService.removeAddress(address.addressId))

            val savedAddress: Address = addressService.createAddress(
              newAddress.firstLine, newAddress.secondLine,
              newAddress.postCode, newAddress.locality,
              newAddress.province, newAddress.country
            )

            team.teamAddress = savedAddress
            teamDao.save(team)
          }
          team
        }
    }.getOrElse(throw new InstanceNotFoundException(teamId, "Team"))
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

  def assignPerson(teamId: Long, personId: Long): StaffMember = {

    val maybePerson: Option[Person] = personService.find(personId)

    if (maybePerson.isEmpty)
      throw new InstanceNotFoundException(personId, "Person")

    val maybeTeam: Option[Team] = find(teamId)

    if (maybeTeam.isEmpty)
      throw new InstanceNotFoundException(teamId, "Team")

    val team: Team = maybeTeam.get
    val person: Person = maybePerson.get

    // If an open relationship between Staff and Team exists
    val maybeStaffMember: Option[StaffMember] =
      findStaffMemberByTeamIdAndPersonId(team.teamId, person.personId)

    maybeStaffMember match {
      case None => {
        // Create new element
        val staffMember = new StaffMember(person, team)
        staffMemberDao.save(staffMember)
        staffMember
      }
      case Some(staffMember) => {
        // Throw
        throw new DuplicateInstanceException(staffMember, "StaffMember")
      }
    }
  }

  @throws[InstanceNotFoundException]("If team, person or staffMember doesn't exist")
  def unAssignStaff(teamId: Long, personId: Long): StaffMember = {

    findStaffMemberByTeamIdAndPersonId(teamId, personId).map {
      staffMember =>
        {
          staffMember.exitDate = Calendar.getInstance()
          staffMemberDao.save(staffMember)
          staffMember
        }
    }.getOrElse(throw new InstanceNotFoundException("TeamId: " + teamId + ", PersonId: " + personId, "StaffMember"))
  }

  def getNumberByFederationId(fedId: Long): Long = {
    teamDao.getNumberByFederationId(fedId)
  }

  @throws[InstanceNotFoundException]("If team doesn't exist")
  def removeTeam(teamId: Long) = {

    find(teamId).map { team =>
      {
        teamDao.remove(team)
      }
    }.getOrElse(throw new InstanceNotFoundException(teamId, "Team"))
  }

}

