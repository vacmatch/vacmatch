package main.scala.service.competition

import java.util.Date
import main.scala.model.competition.{League, LeagueDao}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("leagueService")
class LeagueServiceImpl extends LeagueService {

  @Autowired
  var leagueDao: LeagueDao = _

  def createLeague(fedId: Int, leagueName: String, slug: String): League = {
    val l = new League
    l.fedId = fedId
    l.leagueName = leagueName
    l.slug = slug

    // FIXME: Remove the following line
    // leagueDao.save(l)

    return l
  }

  def findActiveByFederation(fedId: Int): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors", "vr")
    val l2 = createLeague(fedId, "T-Rexes", "trex")
    val l3 = createLeague(fedId, "Seniors", "sr")

    return List(l1, l2, l3)
  }

  def findActiveByFederation(fedId: Int, startTime: Date, endTime: Date): Seq[League] = {
    return findActiveByFederation(fedId)
  }

  def findAllByFederation(fedId: Int): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors", "vr")
    val l2 = createLeague(fedId, "T-Rexes", "tr")
    val l3 = createLeague(fedId, "Seniors", "sr")
    val l4 = createLeague(fedId, "Extincts", "ex")

    return List(l1, l2, l3)
  }

  def findById(leagueId: Int): Option[League] = {
    Option (leagueDao.findById (leagueId))

    Option (createLeague(1, "TheFoundLeague!", "slug"))
  }

  def findBySlug(fedId: Int, slug: String): Option[League] = {
    // Option (leagueDao.findBySlug (fedId, slug))

    Option (createLeague(fedId, "TheFouldLeague!", slug))
  }
}
