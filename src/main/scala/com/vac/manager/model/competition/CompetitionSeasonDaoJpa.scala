package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDaoJPA
import java.util.Calendar
import org.springframework.stereotype.Repository
import scala.collection.JavaConverters._

@Repository("competitionSeasonDao")
class CompetitionSeasonDaoJpa
    extends GenericDaoJPA[CompetitionSeason, CompetitionSeasonPK](classOf[CompetitionSeason])
    with CompetitionSeasonDao {

  def findCompetitionWithSeasonsBySlug(fedId: Long, slug: String): Option[Competition] = {
    var q = getEntityManager().createQuery(
      "SELECT l FROM Competition l JOIN FETCH l.seasonList " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug", classOf[Competition]
    )
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)

    val maybeCompetition = q.getResultList()

    if (maybeCompetition.isEmpty())
      return None
    else return Some(maybeCompetition.get(0))
  }

  def findBySlug(fedId: Long, slug: String, year: String): Option[CompetitionSeason] = {
    var q = getEntityManager().createQuery(
      "SELECT s FROM CompetitionSeason s JOIN s.id.competition l " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug " +
        "AND s.id.seasonSlug = :year ", classOf[CompetitionSeason]
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

  def findByFedAndTime(fedId: Long, when: Calendar): Seq[CompetitionSeason] = {
    var q = getEntityManager().createQuery(
      "SELECT s FROM CompetitionSeason s JOIN FETCH s.id.competition " +
        "WHERE s.id.competition.fedId = :fedId " +
        "AND s.startTime <= :when " +
        "AND (s.endTime IS NULL OR s.endTime > :when) ", classOf[CompetitionSeason]
    )
      .setParameter("fedId", fedId)
      .setParameter("when", when)

    return q.getResultList.asScala
  }

}
