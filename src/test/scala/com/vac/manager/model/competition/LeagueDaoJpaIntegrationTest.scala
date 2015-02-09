
package com.vac.manager.model.competition

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import com.vac.manager.Application
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.Assert._
import java.util.Calendar
import com.vac.manager.service.competition.LeagueService
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.generic.exceptions.NotImplementedException
import com.vac.manager.model.team.Team
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.IncorrectDateException
import javax.management.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IncorrectNameException
import org.springframework.test.context.web.WebAppConfiguration
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
  def createAndFindById() = {
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

    assertNotNull(secondLeague)

  }
}


