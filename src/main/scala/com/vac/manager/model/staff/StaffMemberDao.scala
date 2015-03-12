package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.team.Team

trait StaffMemberDao extends GenericDao[StaffMember, java.lang.Long] {

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember]

  def find(teamId: Long, personId: Long): Option[StaffMember]

}

