package com.vac.manager.service.staff

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import com.vac.manager.model.staff.StaffMemberHistoric

trait StaffMemberHistoricService {

  def find(staffHistoricId: Long): Option[StaffMemberHistoric]

  def findActivatedElement(staffId: Long, teamId: Long): Option[StaffMemberHistoric]

  def findActivatedList(teamId: Long): Seq[StaffMemberHistoric]

  @throws[IllegalArgumentException]
  def create(staff: StaffMember, team: Team): Either[Exception, StaffMemberHistoric]

}

