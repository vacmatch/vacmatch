package main.scala.model.competition

import main.scala.model.generic.GenericDaoHibernate
import org.springframework.stereotype.Repository
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Expression

@Repository("leagueDao")
class LeagueDaoJpa extends GenericDaoHibernate[League, java.lang.Long](classOf[League]) with LeagueDao {

  def findBySlug(fedId: Long, slug: String): Option[League] = {
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

    val q = getEntityManager createQuery criteria

    return null
  }

}
