package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ League, LeagueSeason }
import java.util.{ Calendar, Date, GregorianCalendar }
import scala.collection.JavaConverters.asScalaBufferConverter

//@Service("leagueService")
class LeagueServiceMock extends LeagueService {

  def createLeague(fedId: Long, leagueName: String, slug: String): League = {
    val l = new League
    l.fedId = fedId
    l.leagueName = leagueName
    l.slug = slug

    return l
  }

  def modifyLeagueName(fedId: Long, slug: String, newName: String): Option[League] = {
    return Some(createLeague(fedId, slug, newName))
  }

  def modifyLeagueSlug(fedId: Long, oldSlug: String, newSlug: String): Option[League] = {
    return Some(createLeague(fedId, newSlug, "TEST NAME"))
  }

  def findActiveByFederation(fedId: Long): Seq[LeagueSeason] = {
    val today = new GregorianCalendar()
    return findActiveByFederation(fedId, today)
  }

  def findActiveByFederation(fedId: Long, when: Calendar): Seq[LeagueSeason] = {
    var s1 = createSeason(fedId, "vr", "2014", when, when)
    var s2 = createSeason(fedId, "xr", "2014", when, when)
    var s3 = createSeason(fedId, "or", "2014", when, when)

    return List(s1, s2, s3)
  }

  def findAllByFederation(fedId: Long): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors", "vr")
    val l2 = createLeague(fedId, "T-Rexes", "tr")
    val l3 = createLeague(fedId, "Seniors", "sr")
    val l4 = createLeague(fedId, "Extincts", "ex")

    return List(l1, l2, l3)
  }

  def findById(leagueId: Int): Option[League] = {
    Some(createLeague(1, "TheFoundLeague!", "slug"))
  }

  def findBySlug(fedId: Long, slug: String): Option[League] = {
    Some(createLeague(fedId, "TheFouldLeague!", slug))
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): LeagueSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new NoSuchFieldError()

    val s = new LeagueSeason()
    s.id.setLeague(l.get)
    s.id.setSeasonSlug(year)
    s.setStartTime(startTime)
    s.setEndTime(endTime)

    return s
  }

  /**
   * It's free to return the League instead of the Seq[LeagueSeason]
   * naturally because the JOIN must be made in the DB anyway, so all
   * data is already fetched.
   */
  def findSeasonsByLeague(fedId: Long, slug: String): Option[League] = {
    val l1 = createLeague(fedId, "Velociraptors", "vr")

    return Some(l1)
  }

  /**
   * But we can already return just the Seq as a Scala value
   */
  def findSeasonsByLeagueAsSeq(fedId: Long, slug: String): Seq[LeagueSeason] = {
    return findSeasonsByLeague(fedId, slug).get.getSeasonList.asScala
  }

  def findSeasonByLeagueSlug(fedId: Long, slug: String, seasonYear: String): Option[LeagueSeason] = {
    val startCal = new GregorianCalendar()
    val endCal = new GregorianCalendar()
    val endDate = new Date()

    endDate.setYear(endDate.getYear() + 1)
    endCal.setTime(endDate)

    val s1 = createSeason(fedId, slug, seasonYear, startCal, endCal)

    return Some(s1)
  }

  def removeLeagueBySlug(fedId: Long, slug: String): Boolean = {
    return (fedId == 1)
  }

  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[LeagueSeason] = {
    return None
  }
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[LeagueSeason] = {
    return None
  }
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[LeagueSeason] = {
    return None
  }

  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean = {
    return false
  }
}
