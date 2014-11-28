package main.scala.model.competition

import main.scala.model.generic.GenericDaoHibernate
import org.springframework.stereotype.Repository
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Expression

@Repository("leagueSeasonDao")
class LeagueSeasonDaoJpa extends GenericDaoHibernate[LeagueSeason, LeagueSeason.LeagueSeasonPK](classOf[LeagueSeason]) with LeagueSeasonDao {

  def findLeagueWithSeasonsBySlug(fedId: Long, slug: String): Option[League] = {
    var q = getEntityManager().createQuery(
      "SELECT l FROM League l JOIN FETCH l.seasonList " +
        "WHERE l.fedId = :fedId " +
        "AND l.slug = :slug")
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)

    val maybeLeague = q.getResultList()

    if (maybeLeague.isEmpty())
      return None
    else return Some(maybeLeague.get(0).asInstanceOf[League])
  }

  def findBySlug(fedId: Long, slug: String, year: String): Option[LeagueSeason] = {
    var q = getEntityManager().createQuery(
      "SELECT s FROM LeagueSeason s JOIN s.id.league l " +
	"WHERE l.fedId = :fedId " +
        "AND l.slug = :slug " +
        "AND s.id.seasonYear = :year ")
      .setParameter("fedId", fedId)
      .setParameter("slug", slug)
      .setParameter("year", year)

    val maybeSeason = q.getResultList

    if (maybeSeason.isEmpty()) {
      return None
    } else {
      return Some(maybeSeason.get(0).asInstanceOf[LeagueSeason])
    }
  }

}
