package com.vac.manager.model.team

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.competition.LeagueSeasonPK

trait TeamDao extends GenericDao[Team, java.lang.Long] {

  def findByTeamName(name: String): Team

  def findTeamsByFederationId(fedId: Long): Seq[Team]

  def findTeamsByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[Team]

  def hasCompetitions(id: Long): Boolean

  def getNumberByFederationId(fedId: Long): Long

}