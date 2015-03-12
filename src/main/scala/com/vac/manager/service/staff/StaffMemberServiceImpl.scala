package com.vac.manager.service.staff

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import com.vac.manager.model.staff.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.vac.manager.model.staff.StaffMemberDao
import java.util.Calendar
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException

@Service("staffMemberService")
@Transactional
class StaffMemberServiceImpl extends StaffMemberService {

  @Autowired
  var staffMemberDao: StaffMemberDao = _

  def find(staffMemberId: Long): Option[StaffMember] = {
    staffMemberDao.findById(staffMemberId)
  }

  def find(teamId: Long, personId: Long): Option[StaffMember] = {
    staffMemberDao.find(teamId, personId)
  }

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember] = {
    staffMemberDao.findCurrentStaffMemberListByTeam(teamId)
  }

  @throws[IllegalArgumentException]
  def create(person: Person, team: Team): Either[Exception, StaffMember] = {
    if (Option(person).isEmpty)
      return Left(new IllegalArgumentException(person, classOf[Person].getName()))

    if (Option(team).isEmpty)
      return Left(new IllegalArgumentException(team, classOf[Team].getName()))

    // If an open relationship between Staff and Team exists
    val maybeStaffMember: Option[StaffMember] = find(team.teamId, person.personId)

    maybeStaffMember match {
      case None => {
        // Create new element
        val staffMember = new StaffMember(person, team)
        staffMemberDao.save(staffMember)
        Right(staffMember)
      }
      case Some(staffMember) => {
        // Throw
        Left(new DuplicateInstanceException())
      }
    }
  }
}

