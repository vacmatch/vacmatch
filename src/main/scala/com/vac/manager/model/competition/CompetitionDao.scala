package com.vac.manager.model.competition

import java.lang.Long
import com.vac.manager.model.generic.GenericDao

trait CompetitionDao extends GenericDao[Competition, java.lang.Long] {
  def findAllByFedId(fedId: Long): Seq[Competition]
  def findBySlug(fedId: Long, slug: String): Option[Competition]
}
