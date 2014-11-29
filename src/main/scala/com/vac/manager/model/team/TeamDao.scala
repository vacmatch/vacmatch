package com.vac.manager.model.team

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.competition.Competition

trait TeamDao extends GenericDao[Team, java.lang.Long]{

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findByTeamName(name: String): Team
  
  def hasCompetitions(id: Long): Boolean

  def getNumberByFederationId(fedId: Long): Long
  
}