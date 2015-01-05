package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ League, LeagueDao, LeagueSeason, LeagueSeasonDao, LeagueSeasonPK }
import java.util.{ Calendar, GregorianCalendar }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConverters.asScalaBufferConverter

@Service("leagueService")
@Transactional
class LeagueServiceImpl extends LeagueService {

  @Autowired
  var leagueDao: LeagueDao = _

  @Autowired
  var leagueSeasonDao: LeagueSeasonDao = _

  def createLeague(fedId: Long, leagueName: String, slug: String): League = {
    val l = new League
    l.fedId = fedId
    l.leagueName = leagueName
    l.slug = slug

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
    Option(leagueDao.findById(leagueId))
  }

  def findBySlug(fedId: Long, slug: String): Option[League] = {
    //leagueDao.findBySlug(fedId, slug)
    leagueDao.findBySlug(fedId, slug)
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): LeagueSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new NoSuchFieldError()

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
    return leagueSeasonDao.findLeagueWithSeasonsBySlug(fedId, slug).get.getSeasonList.asScala
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

  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[LeagueSeason] = {
    val season = leagueSeasonDao findBySlug (fedId, slug, oldYear)

    if (season.isDefined) {
      season.get.id.seasonSlug = newYear
      leagueSeasonDao save season.get

    }
    return season
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
}
