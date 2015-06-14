package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDaoJPA
import java.lang.Long
import javax.persistence.criteria._
import org.springframework.stereotype.Repository
import scala.collection.JavaConverters.asScalaBufferConverter

@Repository("competitionDao")
class CompetitionDaoJpa extends GenericDaoJPA[Competition, java.lang.Long](classOf[Competition]) with CompetitionDao {

  def findAllByFedId(fedId: Long): Seq[Competition] = {
    val cb = getEntityManager.getCriteriaBuilder
    var criteria = cb createQuery (classOf[Competition])

    val root = criteria from (classOf[Competition])
    val cond =
      cb.and(
        Array(
          cb.equal(root get ("fedId"), fedId): Predicate
        ): _*
      )

    criteria = criteria select root where (Array(cond): _*)

    val q = getEntityManager createQuery criteria

    return q.getResultList().asScala
  }

  def findBySlug(fedId: Long, slug: String): Option[Competition] = {

    var q = getEntityManager().createQuery(
      "SELECT l FROM Competition l " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug", classOf[Competition]
    )
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)

    val maybeCompetition = q.getResultList()

    return if (maybeCompetition.isEmpty())
      None
    else Some(maybeCompetition.get(0))

    ///////////////////////////////////////

    val cb = getEntityManager.getCriteriaBuilder
    var criteria = cb createQuery (classOf[Competition])

    val root = criteria from (classOf[Competition])
    val cond =
      cb.and(
        Array(
          cb.equal(root get ("fedId"), fedId): Predicate,
          cb.equal(root get ("slug"), slug): Predicate
        ): _*
      )

    criteria = criteria select root where (Array(cond): _*)

    val xq = getEntityManager createQuery criteria

    val r = xq.getResultList()

    return if (r.size == 0) {
      None
    } else {
      Some(r.get(0))
    }
  }

}
