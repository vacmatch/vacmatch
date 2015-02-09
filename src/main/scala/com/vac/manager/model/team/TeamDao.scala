package com.vac.manager.model.team

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.competition.Competition

trait TeamDao extends GenericDao[Team, java.lang.Long]{

  def findByTeamName(name: String): Team
  
  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team]

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team]

  def hasCompetitions(id: Long): Boolean

  def getNumberByFederationId(fedId: Long): Long
  
}