package com.vac.manager.model.game.soccer

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import com.vac.manager.model.competition.LeagueSeasonPK
import scala.collection.JavaConverters._
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Table
import com.vac.manager.model.game.SoccerClassificationEntry

@Repository("teamActDao")
class SoccerActDaoJPA extends GenericDaoJPA[SoccerAct, java.lang.Long](classOf[SoccerAct])
    with SoccerActDao {

  def findByGameId(gameId: Long): Option[SoccerAct] = {
    var result = getEntityManager().createQuery(
      "SELECT sa FROM SoccerAct sa " +
        "WHERE sa.game.gameId = :gameId", classOf[SoccerAct]
    )
      .setParameter("gameId", gameId)
      .getResultList()

    if (result.isEmpty()) {
      None
    } else {
      Some(result.get(0))
    }
  }

  def findAllBySeason(leagueSeasonId: LeagueSeasonPK): Seq[SoccerAct] = {

    var query = getEntityManager().createQuery(
      "SELECT sa FROM SoccerAct sa JOIN sa.game.leagueSeason " +
        "WHERE sa.game.leagueSeason.id = :leagueSeasonId", classOf[SoccerAct]
    )
      .setParameter("leagueSeasonId", leagueSeasonId)

    query.getResultList().asScala
  }

  def getLocalEntry(teamId: Long, leagueSeasonId: LeagueSeasonPK): SoccerClassificationEntry = {
    var result = getEntityManager().createQuery(
      "SELECT NEW com.vac.manager.model.game.SoccerClassificationEntry(" +
        /*Position*/ "COUNT(*), " +
        /*Team*/ "sa.localTeam, " +
        /*Played*/ "COUNT(*), " +
        /*Win*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.localTeam.teamId = :teamId AND s.localResult > s.visitorResult), " +
        /*Drawn*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.localTeam.teamId = :teamId AND s.localResult = s.visitorResult), " +
        /*Lost*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.localTeam.teamId = :teamId AND s.localResult < s.visitorResult), " +
        /*GoalsF*/ "coalesce(SUM(sa.localResult),0), " +
        /*GoalsA*/ "coalesce(SUM(sa.visitorResult),0), " +
        /*GoalD*/ "coalesce((SUM(sa.localResult) - SUM(sa.visitorResult)),0), " +
        /*Points*/ "((SELECT COUNT(*) FROM SoccerAct s WHERE s.localTeam.teamId = :teamId AND s.localResult > s.visitorResult) * 3) " +
        "+ ((SELECT COUNT(*) FROM SoccerAct s WHERE s.localTeam.teamId = :teamId AND s.localResult = s.visitorResult) * 1)) " +
        "FROM SoccerAct sa " +
        "WHERE sa.localTeam IS NOT NULL " +
        "AND sa.localTeam.teamId = :teamId " +
        "AND sa.game.leagueSeason.id = :leagueSeasonId", classOf[SoccerClassificationEntry]
    )
      .setParameter("teamId", teamId)
      .setParameter("leagueSeasonId", leagueSeasonId)
      .getResultList()

    val element = result.get(0)
    element.assessment = element.points
    element
  }

  def getVisitorEntry(teamId: Long, leagueSeasonId: LeagueSeasonPK): SoccerClassificationEntry = {
    var result = getEntityManager().createQuery(
      "SELECT NEW com.vac.manager.model.game.SoccerClassificationEntry(" +
        /*Position*/ "COUNT(*), " +
        /*Team*/ "sa.visitorTeam, " +
        /*Played*/ "COUNT(*), " +
        /*Win*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.visitorTeam.teamId = :teamId AND s.visitorResult > s.localResult), " +
        /*Drawn*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.visitorTeam.teamId = :teamId AND s.visitorResult = s.localResult), " +
        /*Lost*/ "(SELECT COUNT(*) FROM SoccerAct s WHERE s.visitorTeam.teamId = :teamId AND s.visitorResult < s.localResult), " +
        /*GoalsF*/ "coalesce(SUM(sa.visitorResult),0), " +
        /*GoalsA*/ "coalesce(SUM(sa.localResult),0), " +
        /*GoalD*/ "coalesce((SUM(sa.visitorResult) - SUM(sa.localResult)),0), " +
        /*Points*/ "((SELECT COUNT(*) FROM SoccerAct s WHERE s.visitorTeam.teamId = :teamId AND s.visitorResult > s.localResult) * 3) " +
        "+ ((SELECT COUNT(*) FROM SoccerAct s WHERE s.visitorTeam.teamId = :teamId AND s.visitorResult = s.localResult) * 1)) " +
        "FROM SoccerAct sa " +
        "WHERE sa.visitorTeam IS NOT NULL " +
        "AND sa.visitorTeam.teamId = :teamId " +
        "AND sa.game.leagueSeason.id = :leagueSeasonId", classOf[SoccerClassificationEntry]
    )
      .setParameter("teamId", teamId)
      .setParameter("leagueSeasonId", leagueSeasonId)
      .getResultList()

    val element = result.get(0)
    element.assessment = element.points
    element
  }

}

