package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ Competition, CompetitionSeason }
import java.util.Calendar
import com.vac.manager.model.competition.CompetitionSeasonPK
import com.vac.manager.model.competition.CompetitionMember

trait CompetitionService {
  def createCompetition(fedId: Long, competitionName: String, slug: String): Competition
  def modifyCompetitionName(fedId: Long, slug: String, newName: String): Option[Competition]
  def modifyCompetitionSlug(fedId: Long, oldSlug: String, newSlug: String): Option[Competition]
  def findActiveByFederation(fedId: Long): Seq[CompetitionSeason]
  def findActiveByFederation(fedId: Long, when: Calendar): Seq[CompetitionSeason]
  def findAllByFederation(fedId: Long): Seq[Competition]
  def findById(competitionId: Int): Option[Competition]
  def findBySlug(fedId: Long, slug: String): Option[Competition]
  def removeCompetitionBySlug(fedId: Long, slug: String): Boolean

  def createSeason(fedId: Long, slug: String, seasonYear: String, startTime: Calendar, endTime: Calendar): CompetitionSeason
  def findSeasonsByCompetition(fedId: Long, slug: String): Option[Competition]
  def findSeasonsByCompetitionAsSeq(fedId: Long, slug: String): Seq[CompetitionSeason]
  def findSeasonByCompetitionSlug(fedId: Long, slug: String, seasonYear: String): Option[CompetitionSeason]
  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[CompetitionSeason]
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[CompetitionSeason]
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[CompetitionSeason]
  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean

  def findCompetitionMember(competitionSeasonId: Long): Option[CompetitionMember]

  /**
    * Get the unique Competition Member for this CompetitionSeasonId and TeamId which endDate is null.
    * @param competitionSeasonId The identifier from a CompetitionSeason
    * @param teamId The identifier from a Team
    * @return The actual active CompetitionMember between a CompetitionSeason and a Team
    */
  def findCompetitionMember(competitionSeasonId: CompetitionSeasonPK, teamId: Long): Option[CompetitionMember]

  /**
    * Get all Competition Members for this CompetitionSeasonId which endDate is null.
    * @param competitionSeasonId The identifier from a CompetitionSeason
    * @param teamId The identifier from a Team
    * @return All CompetitionMembers from a CompetitionSeason which are active
    */
  def findCompetitionMembersByCompetitionSeasonId(competitionSeasonId: CompetitionSeasonPK): Seq[CompetitionMember]

  /**
    * Create a new CompetitionMember between Team and CompetitionSeason.
    * @param competitionSeasonId The identifier from a CompetitionSeason
    * @param teamId The identifier from a Team
    * @throw DuplicateInstanceException If this relationship between CompetitionSeason and Team, exists previously
    * @throw InstanceNotFoundException If CompetitionSeason, Team or both doesn't exist
    * @return The new CompetitionMember registered from a CompetitionSeason and Team relationship
    */
  def registerTeamInSeason(competitionSeasonId: CompetitionSeasonPK, teamId: Long): CompetitionMember

  /**
    * Set endDate from CompetitionMember identified by Team and CompetitionSeason to actual date.
    * @param competitionSeasonId The identifier from a CompetitionSeason
    * @param teamId The identifier from a Team
    * @throw InstanceNotFoundException If CompetitionSeason, Team or both doesn't exist
    * @return The new CompetitionMember registered from a CompetitionSeason and Team relationship
    */
  def removeTeamFromSeason(competitionSeasonId: CompetitionSeasonPK, teamId: Long): CompetitionMember

}
