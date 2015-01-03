package com.vac.manager.model.competition

import java.util.Calendar
import com.vac.manager.model.generic.GenericDao

trait LeagueSeasonDao extends GenericDao[LeagueSeason, LeagueSeason.LeagueSeasonPK] {
  def findLeagueWithSeasonsBySlug(fedId: Long, slug: String): Option[League]
  def findBySlug(fedId: Long, slug: String, year: String): Option[LeagueSeason]
  def findByFedAndTime(fedId: Long, when: Calendar): Seq[LeagueSeason]
}
