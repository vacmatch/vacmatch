package com.vac.manager.service.staff

import com.vac.manager.model.staff.Person
import com.vac.manager.model.team.Team
import com.vac.manager.model.staff.StaffMember

trait StaffMemberService {

  def find(staffMemberId: Long): Option[StaffMember]

  def findActivatedElement(personId: Long, teamId: Long): Option[StaffMember]

  def findActivatedList(teamId: Long): Seq[StaffMember]

  @throws[IllegalArgumentException]
  def create(person: Person, team: Team): Either[Exception, StaffMember]

}

