package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ Competition, CompetitionDao, CompetitionSeason, CompetitionSeasonDao, CompetitionSeasonPK }
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import java.util.{ Calendar, GregorianCalendar }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConverters.asScalaBufferConverter
import com.vac.manager.service.team.TeamService
import com.vac.manager.model.competition.CompetitionMember
import com.vac.manager.model.competition.CompetitionMemberDao
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

@Service("competitionService")
@Transactional
class CompetitionServiceImpl extends CompetitionService {

  @Autowired
  var competitionDao: CompetitionDao = _

  @Autowired
  var competitionSeasonDao: CompetitionSeasonDao = _

  @Autowired
  var competitionMemberDao: CompetitionMemberDao = _

  @Autowired
  var teamService: TeamService = _

  def createCompetition(fedId: Long, competitionName: String, slug: String): Competition = {
    val l = new Competition
    l.fedId = fedId
    l.competitionName = competitionName
    l.slug = slug

    if (competitionDao.findBySlug(fedId, slug).isDefined)
      throw new DuplicateInstanceException(slug, "Slug")

    competitionDao.save(l)

    return l
  }

  def modifyCompetitionName(fedId: Long, slug: String, newName: String): Option[Competition] = {

    findBySlug(fedId, slug) match {
      case None =>
        return None

      case Some(l) =>
        l.competitionName = newName
        competitionDao.save(l)

        return Some(l)
    }
  }

  def modifyCompetitionSlug(fedId: Long, oldSlug: String, newSlug: String): Option[Competition] = {

    findBySlug(fedId, oldSlug) match {
      case None =>
        return None

      case Some(l) =>
        l.slug = newSlug

        try {
          competitionDao.save(l)
        } catch {
          case e: Exception =>
            return None
        }
        return Some(l)
    }
  }

  def findActiveByFederation(fedId: Long): Seq[CompetitionSeason] = {
    val today = new GregorianCalendar()
    return findActiveByFederation(fedId, today)
  }

  def findActiveByFederation(fedId: Long, when: Calendar): Seq[CompetitionSeason] = {
    return competitionSeasonDao.findByFedAndTime(fedId, when)
  }

  def findAllByFederation(fedId: Long): Seq[Competition] = {
    return competitionDao.findAllByFedId(fedId)
  }

  def findById(competitionId: Int): Option[Competition] = {
    return competitionDao.findById(competitionId)
  }

  def findBySlug(fedId: Long, slug: String): Option[Competition] = {
    return competitionDao.findBySlug(fedId, slug)
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): CompetitionSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new InstanceNotFoundException("FedId: " + fedId + " Slug: " + slug, "Competition")

    if (Option(startTime).isEmpty)
      throw new com.vac.manager.model.generic.exceptions.IllegalArgumentException(startTime, "startTime")

    val s = new CompetitionSeason()
    s.id = new CompetitionSeasonPK()
    s.id.setCompetition(l.get)
    s.id.setSeasonSlug(year)
    s.startTime = startTime
    s.endTime = endTime

    competitionSeasonDao.save(s)

    return s
  }

  /**
    * It's free to return the Competition instead of the Seq[CompetitionSeason]
    * naturally because the JOIN must be made in the DB anyway, so all
    * data is already fetched.
    */
  def findSeasonsByCompetition(fedId: Long, slug: String): Option[Competition] = {
    return competitionSeasonDao.findCompetitionWithSeasonsBySlug(fedId, slug)
  }

  /**
    * But we can already return just the Seq as a Scala value
    */
  def findSeasonsByCompetitionAsSeq(fedId: Long, slug: String): Seq[CompetitionSeason] = {
    return competitionSeasonDao.findCompetitionWithSeasonsBySlug(fedId, slug).map(_.getSeasonList.asScala).getOrElse(List())
  }

  def findSeasonByCompetitionSlug(fedId: Long, slug: String, seasonYear: String): Option[CompetitionSeason] = {
    return competitionSeasonDao.findBySlug(fedId, slug, seasonYear)
  }

  /**
    * Only deletes the competition if it has no seasons
    */
  def removeCompetitionBySlug(fedId: Long, slug: String): Boolean = {
    val competition = competitionDao findBySlug (fedId, slug)

    if (competition.isEmpty) {
      return false
    }

    if ((competition.get.seasonList.size) > 0) {
      return false
    }

    competitionDao remove competition.get

    return true
  }

  @Transactional
  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[CompetitionSeason] = {
    val maybeSeason = competitionSeasonDao findBySlug (fedId, slug, oldYear)

    return maybeSeason match {
      case None => None
      case Some(oldSeason) =>
        /* In JPA you cannot update the id column, so a delete plus recreate is needed */
        val season = new CompetitionSeason
        season.id = new CompetitionSeasonPK
        season.id.setCompetition(oldSeason.id.getCompetition)
        season.id setSeasonSlug newYear
        season.startTime = oldSeason.startTime
        season.endTime = oldSeason.endTime

        competitionSeasonDao remove oldSeason
        competitionSeasonDao save season
        Some(season)
    }
  }
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[CompetitionSeason] = {
    val season = competitionSeasonDao findBySlug (fedId, slug, seasonYear)

    if (season.isDefined) {
      season.get.startTime = startTime
      competitionSeasonDao save season.get

    }
    return season
  }
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[CompetitionSeason] = {
    val season = competitionSeasonDao findBySlug (fedId, slug, seasonYear)

    if (season.isDefined) {
      season.get.endTime = endTime
      competitionSeasonDao save season.get

    }
    return season
  }

  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean = {
    val season = competitionSeasonDao findBySlug (fedId, slug, seasonYear)

    return if (season.isDefined) {
      competitionSeasonDao remove season.get
      true
    } else {
      false
    }
  }

  def findCompetitionMember(compMembId: Long): Option[CompetitionMember] = {
    competitionMemberDao.findById(compMembId)
  }

  def findCompetitionMember(competitionSeasonId: CompetitionSeasonPK, teamId: Long): Option[CompetitionMember] = {
    competitionMemberDao.findCompetitionMemberByCompetitionSeasonIdAndTeamId(competitionSeasonId, teamId)
  }

  def findCompetitionMembersByCompetitionSeasonId(competitionSeasonId: CompetitionSeasonPK): Seq[CompetitionMember] = {
    competitionMemberDao.findCompetitionMembersByCompetitionSeasonId(competitionSeasonId)
  }

  @throws[DuplicateInstanceException]
  @throws[InstanceNotFoundException]
  def registerTeamInSeason(competitionSeasonId: CompetitionSeasonPK, teamId: Long): CompetitionMember = {
    teamService.find(teamId).map {
      team =>
        findSeasonByCompetitionSlug(competitionSeasonId.competition.fedId, competitionSeasonId.competition.slug, competitionSeasonId.seasonSlug).map {
          competition =>
            {
              findCompetitionMember(competitionSeasonId, teamId).map(compMember => throw new DuplicateInstanceException(compMember, "CompetitionMember"))

              val compMember = new CompetitionMember(Calendar.getInstance, competition, team)
              competitionMemberDao.save(compMember)
              compMember
            }
        }.getOrElse(throw new InstanceNotFoundException(competitionSeasonId, "CompetitionSeason"))
    }.getOrElse(throw new InstanceNotFoundException(teamId, "Team"))
  }

  @throws[InstanceNotFoundException]
  def removeTeamFromSeason(competitionSeasonId: CompetitionSeasonPK, teamId: Long): CompetitionMember = {
    findCompetitionMember(competitionSeasonId, teamId).map {
      compMember =>
        {
          compMember.endDate = Calendar.getInstance()
          competitionMemberDao.save(compMember)
          compMember
        }
    }.getOrElse(throw new InstanceNotFoundException("competitionSeasonId:" + competitionSeasonId + "teamId: " + teamId, "CompetitionMember"))
  }

}
