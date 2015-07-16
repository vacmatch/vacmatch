package com.vac.manager.model.game

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository
import com.vac.manager.model.competition.CompetitionSeasonPK
import scala.collection.JavaConverters._

@Repository("gameDao")
class GameDaoJPA
    extends GenericDaoJPA[Game, java.lang.Long](classOf[Game])
    with GameDao {

  def findAllBySeason(competitionSeasonId: CompetitionSeasonPK): Seq[Game] = {
    var query = getEntityManager().createQuery(
      "SELECT g FROM Game g " +
        "WHERE g.competitionSeason.id = :competitionSeasonId", classOf[Game]
    )
      .setParameter("competitionSeasonId", competitionSeasonId)

    query.getResultList().asScala
  }

}
