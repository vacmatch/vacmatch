package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDaoHibernate
import java.util.Calendar
import org.springframework.stereotype.Repository
import scala.collection.JavaConverters._

@Repository("leagueSeasonDao")
class LeagueSeasonDaoJpa extends GenericDaoHibernate[LeagueSeason, LeagueSeasonPK](classOf[LeagueSeason]) with LeagueSeasonDao {

  def findLeagueWithSeasonsBySlug(fedId: Long, slug: String): Option[League] = {
    var q = getEntityManager().createQuery(
      "SELECT l FROM League l JOIN FETCH l.seasonList " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug", classOf[League]
    )
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)

    val maybeLeague = q.getResultList()

    if (maybeLeague.isEmpty())
      return None
    else return Some(maybeLeague.get(0))
  }

  def findBySlug(fedId: Long, slug: String, year: String): Option[LeagueSeason] = {
    var q = getEntityManager().createQuery(
      "SELECT s FROM LeagueSeason s JOIN s.id.league l " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug " +
        "AND s.id.seasonSlug = :year ", classOf[LeagueSeason]
    )
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)
      .setParameter("year", year)

    val maybeSeason = q.getResultList

    if (maybeSeason.isEmpty()) {
      return None
    } else {
      return Some(maybeSeason.get(0))
    }
  }

  def findByFedAndTime(fedId: Long, when: Calendar): Seq[LeagueSeason] = {
    var q = getEntityManager().createQuery(
      "SELECT s FROM LeagueSeason s JOIN FETCH s.id.league " +
        "WHERE s.id.league.fedId = :fedId " +
        "AND s.startTime <= :when " +
        "AND (s.endTime IS NULL OR s.endTime > :when) ", classOf[LeagueSeason]
    )
      .setParameter("fedId", fedId)
      .setParameter("when", when)

    return q.getResultList.asScala
  }

}
