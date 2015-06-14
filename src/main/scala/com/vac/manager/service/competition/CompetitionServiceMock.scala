package com.vac.manager.service.competition

import com.vac.manager.model.competition.{ Competition, CompetitionSeason }
import java.util.{ Calendar, Date, GregorianCalendar }
import scala.collection.JavaConverters.asScalaBufferConverter

//@Service("competitionService")
abstract class CompetitionServiceMock extends CompetitionService {

  def createCompetition(fedId: Long, competitionName: String, slug: String): Competition = {
    val l = new Competition
    l.fedId = fedId
    l.competitionName = competitionName
    l.slug = slug

    return l
  }

  def modifyCompetitionName(fedId: Long, slug: String, newName: String): Option[Competition] = {
    return Some(createCompetition(fedId, slug, newName))
  }

  def modifyCompetitionSlug(fedId: Long, oldSlug: String, newSlug: String): Option[Competition] = {
    return Some(createCompetition(fedId, newSlug, "TEST NAME"))
  }

  def findActiveByFederation(fedId: Long): Seq[CompetitionSeason] = {
    val today = new GregorianCalendar()
    return findActiveByFederation(fedId, today)
  }

  def findActiveByFederation(fedId: Long, when: Calendar): Seq[CompetitionSeason] = {
    var s1 = createSeason(fedId, "vr", "2014", when, when)
    var s2 = createSeason(fedId, "xr", "2014", when, when)
    var s3 = createSeason(fedId, "or", "2014", when, when)

    return List(s1, s2, s3)
  }

  def findAllByFederation(fedId: Long): Seq[Competition] = {
    val l1 = createCompetition(fedId, "Velociraptors", "vr")
    val l2 = createCompetition(fedId, "T-Rexes", "tr")
    val l3 = createCompetition(fedId, "Seniors", "sr")
    val l4 = createCompetition(fedId, "Extincts", "ex")

    return List(l1, l2, l3)
  }

  def findById(competitionId: Int): Option[Competition] = {
    Some(createCompetition(1, "TheFoundCompetition!", "slug"))
  }

  def findBySlug(fedId: Long, slug: String): Option[Competition] = {
    Some(createCompetition(fedId, "TheFouldCompetition!", slug))
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): CompetitionSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new NoSuchFieldError()

    val s = new CompetitionSeason()
    s.id.setCompetition(l.get)
    s.id.setSeasonSlug(year)
    s.setStartTime(startTime)
    s.setEndTime(endTime)

    return s
  }

  /**
    * It's free to return the Competition instead of the Seq[CompetitionSeason]
    * naturally because the JOIN must be made in the DB anyway, so all
    * data is already fetched.
    */
  def findSeasonsByCompetition(fedId: Long, slug: String): Option[Competition] = {
    val l1 = createCompetition(fedId, "Velociraptors", "vr")

    return Some(l1)
  }

  /**
    * But we can already return just the Seq as a Scala value
    */
  def findSeasonsByCompetitionAsSeq(fedId: Long, slug: String): Seq[CompetitionSeason] = {
    return findSeasonsByCompetition(fedId, slug).get.getSeasonList.asScala
  }

  def findSeasonByCompetitionSlug(fedId: Long, slug: String, seasonYear: String): Option[CompetitionSeason] = {
    val startCal = new GregorianCalendar()
    val endCal = new GregorianCalendar()
    val endDate = new Date()

    endDate.setYear(endDate.getYear() + 1)
    endCal.setTime(endDate)

    val s1 = createSeason(fedId, slug, seasonYear, startCal, endCal)

    return Some(s1)
  }

  def removeCompetitionBySlug(fedId: Long, slug: String): Boolean = {
    return (fedId == 1)
  }

  def modifySeasonYearBySlug(fedId: Long, slug: String, oldYear: String, newYear: String): Option[CompetitionSeason] = {
    return None
  }
  def modifySeasonStartTimeBySlug(fedId: Long, slug: String, seasonYear: String, startTime: Calendar): Option[CompetitionSeason] = {
    return None
  }
  def modifySeasonEndTimeBySlug(fedId: Long, slug: String, seasonYear: String, endTime: Calendar): Option[CompetitionSeason] = {
    return None
  }

  def removeSeasonBySlug(fedId: Long, slug: String, seasonYear: String): Boolean = {
    return false
  }
}
