package main.scala.model.competition

import main.scala.model.generic.GenericDaoHibernate
import org.springframework.stereotype.Repository
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Expression
import scala.collection.JavaConverters._

@Repository("leagueDao")
class LeagueDaoJpa extends GenericDaoHibernate[League, java.lang.Long](classOf[League]) with LeagueDao {

  def findAllByFedId(fedId: Long): Seq[League] = {
    val cb = getEntityManager.getCriteriaBuilder
    var criteria = cb createQuery (classOf[League])

    val root = criteria from (classOf[League])
    val cond =
      cb.and(
        Array(
          cb.equal(root get ("fedId"), fedId): Predicate
        ):_*
      )

    criteria = criteria select root where (Array(cond):_*)

    val q = getEntityManager createQuery criteria

    return q.getResultList().asScala
  }

  def findBySlug(fedId: Long, slug: String): Option[League] = {

    var q = getEntityManager().createQuery(
      "SELECT l FROM League l " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug", classOf[League])
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)

    val maybeLeague = q.getResultList()

    return if (maybeLeague.isEmpty())
      None
    else Some(maybeLeague.get(0))

    ///////////////////////////////////////

    val cb = getEntityManager.getCriteriaBuilder
    var criteria = cb createQuery (classOf[League])

    val root = criteria from (classOf[League])
    val cond =
      cb.and(
        Array(
          cb.equal(root get ("fedId"), fedId): Predicate,
          cb.equal(root get ("slug"), slug):Predicate
        ):_*
      )

    criteria = criteria select root where (Array(cond):_*)

    val xq = getEntityManager createQuery criteria

    val r = xq.getResultList()

    return if (r.size == 0) {
      None
    } else {
      Some (r.get(0))
    }
  }

}
