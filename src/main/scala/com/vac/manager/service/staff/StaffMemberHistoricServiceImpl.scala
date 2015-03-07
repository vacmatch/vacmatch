package com.vac.manager.service.staff

import com.vac.manager.model.staff.StaffMemberHistoric
import com.vac.manager.model.team.Team
import com.vac.manager.model.staff.StaffMember
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.vac.manager.model.staff.StaffMemberHistoricDao
import java.util.Calendar
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException

@Service("staffMemberHistoricService")
@Transactional
class StaffMemberHistoricServiceImpl extends StaffMemberHistoricService {

  @Autowired
  var staffHistoricDao: StaffMemberHistoricDao = _

  def find(staffHistoricId: Long): Option[StaffMemberHistoric] = {
    staffHistoricDao.findById(staffHistoricId)
  }

  def findActivatedElement(staffId: Long, teamId: Long): Option[StaffMemberHistoric] = {
    staffHistoricDao.findActivatedElement(staffId, teamId)
  }

  def findActivatedList(teamId: Long): Seq[StaffMemberHistoric] = {
    staffHistoricDao.findActivatedList(teamId)
  }

  @throws[IllegalArgumentException]
  def create(staff: StaffMember, team: Team): Either[Exception, StaffMemberHistoric] = {
    if (staff == null)
      return Left(new IllegalArgumentException(staff, classOf[StaffMember].getName()))

    if (team == null)
      return Left(new IllegalArgumentException(team, classOf[Team].getName()))

    // If an open relationship between Staff and Team exists
    val maybeStaffHistoric: Option[StaffMemberHistoric] = findActivatedElement(staff.staffId, team.teamId)

    maybeStaffHistoric match {
      case None => {
        // Create new element
        val staffHistoric = new StaffMemberHistoric(staff, team)
        staffHistoricDao.save(staffHistoric)
        Right(staffHistoric)
      }
      case Some(staffHistoric) => {
        // Throw
        Left(new DuplicateInstanceException())
      }
    }
  }
}

