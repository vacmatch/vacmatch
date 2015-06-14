package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDao
import java.util.Calendar

trait CompetitionSeasonDao extends GenericDao[CompetitionSeason, CompetitionSeasonPK] {
  def findCompetitionWithSeasonsBySlug(fedId: Long, slug: String): Option[Competition]
  def findBySlug(fedId: Long, slug: String, year: String): Option[CompetitionSeason]
  def findByFedAndTime(fedId: Long, when: Calendar): Seq[CompetitionSeason]
}
