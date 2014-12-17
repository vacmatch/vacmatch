package main.scala.model.competition

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import main.scala.Application
import main.scala.model.competition.League
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.Assert._
import java.util.Calendar
import main.scala.service.competition.LeagueService
import main.scala.model.team.TeamDao
import main.scala.model.generic.exceptions.NotImplementedException
import main.scala.model.team.Team
import main.scala.model.generic.exceptions.DuplicateInstanceException
import main.scala.model.generic.exceptions.IncorrectDateException
import javax.management.InstanceNotFoundException
import main.scala.model.generic.exceptions.IncorrectNameException
import org.springframework.transaction.annotation.Transactional

@RunWith(classOf[SpringJUnit4ClassRunner])
//@ContextConfiguration(locations = Array("classpath:/application.xml", "classpath:/spring-config-test.xml"))
@SpringApplicationConfiguration(classes = Array(classOf[Application]), locations = Array("classpath:/spring-config-test.xml"))
@Transactional
class LeagueDaoJpaIntegrationTest {

  @Autowired
  private var leagueService: LeagueService = _

  @PersistenceContext
  private var entityManager: EntityManager = _


  @Test
  def createAndFindById = {
    val fedId:Long = 1
    val leagueName = "Pepitos Volantes"
    val slug = "pepitos"

    var league = leagueService.createLeague(fedId, leagueName, slug)
    entityManager.flush()

    assertEquals(fedId, league.fedId)
    assertEquals(leagueName, league.leagueName)
    assertEquals(slug, league.slug)

    val l = entityManager.createQuery("SELECT l FROM League l", classOf[League]).getResultList()
    println("XXX GOT VAL " + l)

    var secondLeague = leagueService.findBySlug(fedId, slug)

    assertNotNull(secondLeague)
    assertFalse(secondLeague.isEmpty)

    secondLeague match {
      case None => fail()
      case Some(aLeague) => assertEquals(league, aLeague)
    }

    assertEquals(league, secondLeague)
    assertNotNull(secondLeague)

  }
}


