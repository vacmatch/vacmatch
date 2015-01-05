package com.vac.manager.model.competition

import com.vac.manager.model.generic.GenericDao
import java.util.Calendar

trait LeagueSeasonDao extends GenericDao[LeagueSeason, LeagueSeasonPK] {
  def findLeagueWithSeasonsBySlug(fedId: Long, slug: String): Option[League]
  def findBySlug(fedId: Long, slug: String, year: String): Option[LeagueSeason]
  def findByFedAndTime(fedId: Long, when: Calendar): Seq[LeagueSeason]
}
