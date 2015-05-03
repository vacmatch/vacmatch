package com.vac.manager.model.game.soccer

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import com.vac.manager.model.competition.LeagueSeasonPK
import scala.collection.JavaConverters._
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Table

@Repository("teamActDao")
class SoccerActDaoJPA extends GenericDaoJPA[SoccerAct, java.lang.Long](classOf[SoccerAct])
    with SoccerActDao {

  def findByGameId(gameId: Long): Option[SoccerAct] = {
    var result = getEntityManager().createQuery(
      "SELECT sa FROM SoccerAct sa " +
        "WHERE sa.game.gameId = :gameId", classOf[SoccerAct])
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
      "SELECT sa FROM SoccerAct sa " +
        "WHERE sa.game.leagueSeason.id = :leagueSeasonId", classOf[SoccerAct])
      .setParameter("leagueSeasonId", leagueSeasonId)

    query.getResultList().asScala
  }

}