package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDao

trait CompetitionMemberDao extends GenericDao[CompetitionMember, java.lang.Long] {

  def findCompetitionMembersByCompetitionSeasonId(competitionSeasonId: CompetitionSeasonPK): Seq[CompetitionMember]

  def findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeasonId: CompetitionSeasonPK, teamId: Long): Option[CompetitionMember]

}
