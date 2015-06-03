package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ League, LeagueDao, LeagueSeason, LeagueSeasonDao, LeagueSeasonPK }
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

@Service("leagueService")
@Transactional
class LeagueServiceImpl extends LeagueService {

  @Autowired
  var leagueDao: LeagueDao = _

  @Autowired
  var leagueSeasonDao: LeagueSeasonDao = _

  @Autowired
  var competitionMemberDao: CompetitionMemberDao = _

  @Autowired
  var teamService: TeamService = _

  def createLeague(fedId: Long, leagueName: String, slug: String): League = {
    val l = new League
    l.fedId = fedId
    l.leagueName = leagueName
    l.slug = slug

    if (leagueDao.findBySlug(fedId, slug).isDefined)
      throw new DuplicateInstanceException(slug, "Slug")

    leagueDao.save(l)

    return l
  }

  def modifyLeagueName(fedId: Long, slug: String, newName: String): Option[League] = {

    findBySlug(fedId, slug) match {
      case None =>
        return None

      case Some(l) =>
        l.leagueName = newName
        leagueDao.save(l)

        return Some(l)
    }
  }

  def modifyLeagueSlug(fedId: Long, oldSlug: String, newSlug: String): Option[League] = {

    findBySlug(fedId, oldSlug) match {
      case None =>
        return None

      case Some(l) =>
        l.slug = newSlug

        try {
          leagueDao.save(l)
        } catch {
          case e: Exception =>
            return None
        }
        return Some(l)
    }
  }

  def findActiveByFederation(fedId: Long): Seq[LeagueSeason] = {
    val today = new GregorianCalendar()
    return findActiveByFederation(fedId, today)
  }

  def findActiveByFederation(fedId: Long, when: Calendar): Seq[LeagueSeason] = {
    return leagueSeasonDao.findByFedAndTime(fedId, when)
  }

  def findAllByFederation(fedId: Long): Seq[League] = {
    return leagueDao.findAllByFedId(fedId)
  }

  def findById(leagueId: Int): Option[League] = {
    return leagueDao.findById(leagueId)
  }

  def findBySlug(fedId: Long, slug: String): Option[League] = {
    return leagueDao.findBySlug(fedId, slug)
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): LeagueSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new InstanceNotFoundException("FedId: " + fedId + " Slug: " + slug, "League")

    if (Option(startTime).isEmpty)
      throw new com.vac.manager.model.generic.exceptions.IllegalArgumentException(startTime, "startTime")

    val s = new LeagueSeason()
    s.id = new LeagueSeasonPK()
    s.id.setLeague(l.get)
    s.id.setSeasonSlug(year)
    s.startTime = startTime
    s.endTime = endTime

    leagueSeasonDao.save(s)

    return s
  }

  /**
    * It's free to return the League instead of the Seq[LeagueSeason]
    * naturally because the JOIN must be made in the DB anyway, so all
    * data is already fetched.
    */
  def findSeasonsByLeague(fedId: Long, slug: String): Option[League] = {
    return leagueSeasonDao.findLeagueWithSeasonsBySlug(fedId, slug)
  }

  /**
    * But we can already return just the Seq as a Scala value
    */
  def findSeasonsByLeagueAsSeq(fedId: Long, slug: String): Seq[LeagueSeason] = {
    return leagueSeasonDao.findLeagueWithSeasonsBySlug(fedId, slug).map(_.getSeasonList.asScala).getOrElse(List())
  }

  def findSeasonByLeagueSlug(fedId: Long, slug: String, seasonYear: String): Option[LeagueSeason] = {
    return leagueSeasonDao.findBySlug(fedId, slug, seasonYear)
  }

  /**
    * Only deletes the league if it has no seasons
    */
  def removeLeagueBySlug(fedId: Long, slug: String): Boolean = {
    val league = leagueDao findBySlug (fedId, slug)

    if (league.isEmpty) {
      return false
    }

    if ((league.get.seasonList.size) > 0) {
      return false
    }

    leagueDao remove league.get

    return true
  }

  @Transactional
  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[LeagueSeason] = {
    val maybeSeason = leagueSeasonDao findBySlug (fedId, slug, oldYear)

    return maybeSeason match {
      case None => None
      case Some(oldSeason) =>
        /* In JPA you cannot update the id column, so a delete plus recreate is needed */
        val season = new LeagueSeason
        season.id = new LeagueSeasonPK
        season.id.setLeague(oldSeason.id.getLeague)
        season.id setSeasonSlug newYear
        season.startTime = oldSeason.startTime
        season.endTime = oldSeason.endTime

        leagueSeasonDao remove oldSeason
        leagueSeasonDao save season
        Some(season)
    }
  }
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[LeagueSeason] = {
    val season = leagueSeasonDao findBySlug (fedId, slug, seasonYear)

    if (season.isDefined) {
      season.get.startTime = startTime
      leagueSeasonDao save season.get

    }
    return season
  }
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[LeagueSeason] = {
    val season = leagueSeasonDao findBySlug (fedId, slug, seasonYear)

    if (season.isDefined) {
      season.get.endTime = endTime
      leagueSeasonDao save season.get

    }
    return season
  }

  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean = {
    val season = leagueSeasonDao findBySlug (fedId, slug, seasonYear)

    return if (season.isDefined) {
      leagueSeasonDao remove season.get
      true
    } else {
      false
    }
  }

  def findCompetitionMember(compMembId: Long): Option[CompetitionMember] = {
    competitionMemberDao.findById(compMembId)
  }

  def findCompetitionMember(leagueSeasonId: LeagueSeasonPK, teamId: Long): Option[CompetitionMember] = {
    competitionMemberDao.findCompetitionMemberByLeagueSeasonIdAndTeamId(leagueSeasonId, teamId)
  }

  def findCompetitionMembersByLeagueSeasonId(leagueSeasonId: LeagueSeasonPK): Seq[CompetitionMember] = {
    competitionMemberDao.findCompetitionMembersByLeagueSeasonId(leagueSeasonId)
  }

  @throws[DuplicateInstanceException]
  @throws[InstanceNotFoundException]
  def registerTeamInSeason(leagueSeasonId: LeagueSeasonPK, teamId: Long): CompetitionMember = {
    teamService.find(teamId).map {
      team =>
        findSeasonByLeagueSlug(leagueSeasonId.league.fedId, leagueSeasonId.league.slug, leagueSeasonId.seasonSlug).map {
          league =>
            {
              findCompetitionMember(leagueSeasonId, teamId).map(compMember => throw new DuplicateInstanceException(compMember, "CompetitionMember"))

              val compMember = new CompetitionMember(Calendar.getInstance, league, team)
              competitionMemberDao.save(compMember)
              compMember
            }
        }.getOrElse(throw new InstanceNotFoundException(leagueSeasonId, "LeagueSeason"))
    }.getOrElse(throw new InstanceNotFoundException(teamId, "Team"))
  }

  @throws[InstanceNotFoundException]
  def removeTeamFromSeason(leagueSeasonId: LeagueSeasonPK, teamId: Long): CompetitionMember = {
    findCompetitionMember(leagueSeasonId, teamId).map {
      compMember =>
        {
          compMember.endDate = Calendar.getInstance()
          competitionMemberDao.save(compMember)
          compMember
        }
    }.getOrElse(throw new InstanceNotFoundException("leagueSeasonId:" + leagueSeasonId + "teamId: " + teamId, "CompetitionMember"))
  }

}
