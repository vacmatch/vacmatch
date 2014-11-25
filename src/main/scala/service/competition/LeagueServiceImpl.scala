package main.scala.service.competition

import java.util.Date
import main.scala.model.competition.{League, LeagueDao}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("leagueService")
class LeagueServiceImpl extends LeagueService {

  @Autowired
  var leagueDao: LeagueDao = _

  def createLeague(fedId: Int, leagueName: String): League = {
    val l = new League
    l.fedId = fedId
    l.leagueName = leagueName

    // FIXME: Remove the following line
    // leagueDao.save(l)

    return l
  }

  def findActiveByFederation(fedId: Int): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors")
    val l2 = createLeague(fedId, "T-Rexes")
    val l3 = createLeague(fedId, "Seniors")

    return List(l1, l2, l3)
  }

  def findActiveByFederation(fedId: Int, startTime: Date, endTime: Date): Seq[League] = {
    return findActiveByFederation(fedId)
  }

  def findAllByFederation(fedId: Int): Seq[League] = {
    val l1 = createLeague(fedId, "Velociraptors")
    val l2 = createLeague(fedId, "T-Rexes")
    val l3 = createLeague(fedId, "Seniors")
    val l4 = createLeague(fedId, "Extincts")

    return List(l1, l2, l3)
  }

  def findById(leagueId: Int): Option[League] = {
    Option (leagueDao.findById (leagueId))

    Option (createLeague(1, "TheFoundLeague!"))
  }
}
