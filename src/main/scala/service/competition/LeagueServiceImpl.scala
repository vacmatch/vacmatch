package main.scala.service.competition

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import main.scala.model.competition.{League, LeagueDao, LeagueSeason, LeagueSeasonDao}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._

@Service("leagueService")
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

    // FIXME: Remove the following line
    // leagueDao.save(l)

    return l
  }

  def findActiveByFederation(fedId: Long): Seq[League] = {
    val today = new GregorianCalendar()
    return findActiveByFederation(fedId, today)
  }

  def findActiveByFederation(fedId: Long, when: Calendar): Seq[League] = {
    return null
  }

  def findAllByFederation(fedId: Long): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors", "vr")
    val l2 = createLeague(fedId, "T-Rexes", "tr")
    val l3 = createLeague(fedId, "Seniors", "sr")
    val l4 = createLeague(fedId, "Extincts", "ex")

    return List(l1, l2, l3)
  }

  def findById(leagueId: Int): Option[League] = {
    // Option (leagueDao.findById (leagueId))

    Option (createLeague(1, "TheFoundLeague!", "slug"))
  }

  def findBySlug(fedId: Long, slug: String): Option[League] = {
    // Option (leagueDao.findBySlug (fedId, slug))

    Option (createLeague(fedId, "TheFouldLeague!", slug))
  }


  def createSeason(fedId: Long, slug: String, year: String): LeagueSeason = {
    val l = findBySlug(fedId, slug)
    if (l.isEmpty) throw new NoSuchFieldError()

    val s = new LeagueSeason()
    s.id.setLeague(l.get)
    s.id.setSeasonYear(year)

    return s
  }

 /** It's free to return the League instead of the Seq[LeagueSeason]
   * naturally because the JOIN must be made in the DB anyway, so all
   * data is already fetched.
   */
  def findSeasonsByLeague(fedId: Long, slug: String): Option[League] = {
    return leagueSeasonDao.findLeagueWithSeasonsBySlug(fedId, slug)
  }

 /** But we can already return just the Seq as a Scala value
   */
  def findSeasonsByLeagueAsSeq(fedId: Long, slug: String): Seq[LeagueSeason] = {
    return leagueSeasonDao.findLeagueWithSeasonsBySlug(fedId, slug).get.getSeasonList.asScala
  }

  def findSeasonByLeagueSlug(fedId: Long, slug: String, seasonYear: String): Option[LeagueSeason] = {
    return leagueSeasonDao.findBySlug(fedId, slug, seasonYear)
  }
}
