package main.scala.model.competition

import main.scala.model.generic.GenericDao

trait LeagueDao extends GenericDao[League, java.lang.Long] {
  def findBySlug(fedId: Int, slug: String): Option[League]
}
