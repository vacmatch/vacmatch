package com.vac.manager.model.game

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository
import com.vac.manager.model.competition.LeagueSeasonPK
import scala.collection.JavaConverters._

@Repository("gameDao")
class GameDaoJPA
    extends GenericDaoJPA[Game, java.lang.Long](classOf[Game])
    with GameDao {

  def findAllBySeason(leagueSeasonId: LeagueSeasonPK): Seq[Game] = {
    var query = getEntityManager().createQuery(
      "SELECT g FROM Game g " +
        "WHERE g.leagueSeason.id = :leagueSeasonId", classOf[Game]
    )
      .setParameter("leagueSeasonId", leagueSeasonId)

    query.getResultList().asScala
  }

}
