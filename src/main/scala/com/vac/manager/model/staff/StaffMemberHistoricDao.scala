package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.team.Team

trait StaffMemberHistoricDao extends GenericDao[StaffMemberHistoric, java.lang.Long] {

  def findActivatedList(teamId: Long): Seq[StaffMemberHistoric]

  def findActivatedElement(staffId: Long, teamId: Long): Option[StaffMemberHistoric]

}

