package main.scala.model.competition

import main.scala.model.generic.GenericDao

trait LeagueDao extends GenericDao[League, java.lang.Long] {
  def findAllByFedId(fedId: Long): Seq[League]
  def findBySlug(fedId: Long, slug: String): Option[League]
}
