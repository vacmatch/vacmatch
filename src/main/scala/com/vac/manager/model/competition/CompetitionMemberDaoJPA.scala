package com.vac.manager.model.competition

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import scala.collection.JavaConverters._

@Repository("competitionMemberDao")
class CompetitionMemberDaoJPA
  extends GenericDaoJPA[CompetitionMember, java.lang.Long](classOf[CompetitionMember])
  with CompetitionMemberDao {

  def findCompetitionMembersByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[CompetitionMember] = {
    // TODO Modify to find by competition id
    var q = getEntityManager().createQuery(
      "SELECT cm FROM CompetitionMember cm " +
        "WHERE cm.endDate IS NULL " +
        "AND cm.leagueSeason.id = :leagueSeasonId", classOf[CompetitionMember])
      .setParameter("leagueSeasonId", leagueSeasonId)

    q.getResultList().asScala
  }

  def findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeasonId: LeagueSeasonPK, teamId: Long): Option[CompetitionMember] = {
    // TODO Modify to find by competition id andteamId
    var result = getEntityManager().createQuery(
      "SELECT cm FROM CompetitionMember cm " +
        "WHERE cm.endDate IS NULL " +
        "AND cm.leagueSeason.id = :leagueSeasonId " +
        "AND cm.team.teamId = :teamId", classOf[CompetitionMember])
      .setParameter("leagueSeasonId", leagueSeasonId)
      .setParameter("teamId", teamId)
      .getResultList()

    if (result.isEmpty()) {
      None
    } else {
      Some(result.get(0))
    }
  }

}
