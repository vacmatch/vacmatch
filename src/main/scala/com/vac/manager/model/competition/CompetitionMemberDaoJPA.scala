package com.vac.manager.model.competition

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import scala.collection.JavaConverters._

@Repository("competitionMemberDao")
class CompetitionMemberDaoJPA
    extends GenericDaoJPA[CompetitionMember, java.lang.Long](classOf[CompetitionMember])
    with CompetitionMemberDao {

  def findCompetitionMembersByCompetitionSeasonId(competitionSeasonId: CompetitionSeasonPK): Seq[CompetitionMember] = {
    // TODO Modify to find by competition id
    var q = getEntityManager().createQuery(
      "SELECT cm FROM CompetitionMember cm " +
        "WHERE cm.endDate IS NULL " +
        "AND cm.competitionSeason.id = :competitionSeasonId", classOf[CompetitionMember]
    )
      .setParameter("competitionSeasonId", competitionSeasonId)

    q.getResultList().asScala
  }

  def findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeasonId: CompetitionSeasonPK, teamId: Long): Option[CompetitionMember] = {
    // TODO Modify to find by competition id andteamId
    var result = getEntityManager().createQuery(
      "SELECT cm FROM CompetitionMember cm " +
        "WHERE cm.endDate IS NULL " +
        "AND cm.competitionSeason.id = :competitionSeasonId " +
        "AND cm.team.teamId = :teamId", classOf[CompetitionMember]
    )
      .setParameter("competitionSeasonId", competitionSeasonId)
      .setParameter("teamId", teamId)
      .getResultList()

    if (result.isEmpty()) {
      None
    } else {
      Some(result.get(0))
    }
  }

}
