package com.vac.manager.model.game.soccer

import com.vac.manager.model.generic.GenericDaoJPA
import scala.collection.JavaConverters._
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.Table

@Repository("soccerStaffStatsDao")
class SoccerStaffStatsDaoJPA
  extends GenericDaoJPA[SoccerStaffStats, java.lang.Long](classOf[SoccerStaffStats])
  with SoccerStaffStatsDao {

  def findLocalStatsByActId(actId: Long): Seq[SoccerStaffStats] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM SoccerStaffStats s " +
        "WHERE s.act.id = :actId " +
        "AND s.act.localTeam = s.staffMember.staffTeam", classOf[SoccerStaffStats])
      .setParameter("actId", actId)

    query.getResultList().asScala
  }

  def findVisitorStatsByActId(actId: Long): Seq[SoccerStaffStats] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM SoccerStaffStats s " +
        "WHERE s.act.id = :actId " +
        "AND s.act.visitorTeam = s.staffMember.staffTeam", classOf[SoccerStaffStats])
      .setParameter("actId", actId)

    query.getResultList().asScala
  }

}

