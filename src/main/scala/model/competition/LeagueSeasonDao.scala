package main.scala.model.competition

import java.util.Calendar
import main.scala.model.generic.GenericDao

trait LeagueSeasonDao extends GenericDao[LeagueSeason, LeagueSeason.LeagueSeasonPK] {
  def findLeagueWithSeasonsBySlug(fedId: Long, slug: String): Option[League]
  def findBySlug(fedId: Long, slug: String, year: String): Option[LeagueSeason]
  def findByFedAndTime(fedId: Long, when: Calendar): Seq[LeagueSeason]
}
