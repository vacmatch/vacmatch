package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.team.Team

trait StaffMemberDao extends GenericDao[StaffMember, java.lang.Long] {

  def findActivatedList(teamId: Long): Seq[StaffMember]

  def findActivatedElement(personId: Long, teamId: Long): Option[StaffMember]

}

