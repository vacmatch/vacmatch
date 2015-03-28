package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDao

trait CompetitionMemberDao extends GenericDao[CompetitionMember, java.lang.Long] {

  def findCompetitionMembersByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[CompetitionMember]

  def findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeasonId: LeagueSeasonPK, teamId: Long): Option[CompetitionMember]

}
