package com.vac.manager.model.competition

import java.lang.Long
import com.vac.manager.model.generic.GenericDao

trait LeagueDao extends GenericDao[League, java.lang.Long] {
  def findAllByFedId(fedId: Long): Seq[League]
  def findBySlug(fedId: Long, slug: String): Option[League]
}
