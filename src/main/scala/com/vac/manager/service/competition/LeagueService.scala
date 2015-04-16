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

  /**
   * Get the unique Competition Member for this LeagueSeasonId and TeamId which endDate is null.
   * @param leagueSeasonId The identifier from a LeagueSeason
   * @param teamId The identifier from a Team
   * @return The actual active CompetitionMember between a LeagueSeason and a Team
   */
  def findCompetitionMember(leagueSeasonId: LeagueSeasonPK, teamId: Long): Option[CompetitionMember]

  /**
   * Get all Competition Members for this LeagueSeasonId which endDate is null.
   * @param leagueSeasonId The identifier from a LeagueSeason
   * @param teamId The identifier from a Team
   * @return All CompetitionMembers from a LeagueSeason which are active
   */
  def findCompetitionMembersByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[CompetitionMember]

  /**
   * Create a new CompetitionMember between Team and LeagueSeason.
   * @param leagueSeasonId The identifier from a LeagueSeason
   * @param teamId The identifier from a Team
   * @throw DuplicateInstanceException If this relationship between LeagueSeason and Team, exists previously
   * @throw InstanceNotFoundException If LeagueSeason, Team or both doesn't exist
   * @return The new CompetitionMember registered from a LeagueSeason and Team relationship
   */
  def registerTeamInSeason(leagueSeasonId: LeagueSeasonPK, teamId: Long): CompetitionMember

  /**
   * Set endDate from CompetitionMember identified by Team and LeagueSeason to actual date.
   * @param leagueSeasonId The identifier from a LeagueSeason
   * @param teamId The identifier from a Team
   * @throw InstanceNotFoundException If LeagueSeason, Team or both doesn't exist
   * @return The new CompetitionMember registered from a LeagueSeason and Team relationship
   */
  def removeTeamFromSeason(leagueSeasonId: LeagueSeasonPK, teamId: Long): CompetitionMember

}
