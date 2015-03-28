package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ League, LeagueSeason }
import java.util.Calendar
import com.vac.manager.model.competition.LeagueSeasonPK
import com.vac.manager.model.competition.CompetitionMember

trait LeagueService {
  def createLeague(fedId: Long, leagueName: String, slug: String): League
  def modifyLeagueName(fedId: Long, slug: String, newName: String): Option[League]
  def modifyLeagueSlug(fedId: Long, oldSlug: String, newSlug: String): Option[League]
  def findActiveByFederation(fedId: Long): Seq[LeagueSeason]
  def findActiveByFederation(fedId: Long, when: Calendar): Seq[LeagueSeason]
  def findAllByFederation(fedId: Long): Seq[League]
  def findById(leagueId: Int): Option[League]
  def findBySlug(fedId: Long, slug: String): Option[League]
  def removeLeagueBySlug(fedId: Long, slug: String): Boolean

  def createSeason(fedId: Long, slug: String, seasonYear: String, startTime: Calendar, endTime: Calendar): LeagueSeason
  def findSeasonsByLeague(fedId: Long, slug: String): Option[League]
  def findSeasonsByLeagueAsSeq(fedId: Long, slug: String): Seq[LeagueSeason]
  def findSeasonByLeagueSlug(fedId: Long, slug: String, seasonYear: String): Option[LeagueSeason]
  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[LeagueSeason]
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[LeagueSeason]
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[LeagueSeason]
  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean

  def findCompetitionMember(leagueSeasonId: Long): Option[CompetitionMember]
  def findCompetitionMember(leagueSeasonId: LeagueSeasonPK, teamId: Long): Option[CompetitionMember]
  def findCompetitionMembersByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[CompetitionMember]
  def registerTeamInSeason(leagueSeasonId: LeagueSeasonPK, teamId: Long): CompetitionMember

}
