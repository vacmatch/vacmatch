package com.vac.manager.service.competition

import java.util.Calendar
import java.util.Date
import com.vac.manager.model.competition.League
import com.vac.manager.model.competition.LeagueSeason

trait LeagueService {
  def createLeague(fedId: Long, leagueName: String, slug: String): League
  def modifyLeagueName(fedId: Long, slug: String, newName: String): Option[League]
  def modifyLeagueSlug(fedId: Long, oldSlug: String, newSlug: String): Option[League]
  def findActiveByFederation(fedId: Long): Seq[LeagueSeason]
  def findActiveByFederation(fedId: Long, when: Calendar): Seq[LeagueSeason]
  def findAllByFederation(fedId: Long): Seq[League]
  def findById(leagueId: Int): Option[League]
  def findBySlug(fedId: Long, slug: String): Option[League]


  def createSeason(fedId: Long, slug: String, seasonYear: String, startTime: Calendar, endTime: Calendar): LeagueSeason
  def findSeasonsByLeague(fedId: Long, slug: String): Option[League]
  def findSeasonsByLeagueAsSeq(fedId: Long, slug: String): Seq[LeagueSeason]
  def findSeasonByLeagueSlug(fedId: Long, slug: String, seasonYear: String): Option[LeagueSeason]
}
