package main.scala.service.competition

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import main.scala.model.competition.{ League, LeagueDao, LeagueSeason, LeagueSeasonDao }
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional

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

  @Transactional
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

  @Transactional
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
    leagueDao.findBySlug(fedId, slug)
  }

  def createSeason(fedId: Long, slug: String, year: String, startTime: Calendar, endTime: Calendar): LeagueSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new NoSuchFieldError()

    val s = new LeagueSeason()
    s.id.setLeague(l.get)
    s.id.setSeasonSlug(year)

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
}
