
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
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.model.team.TeamDao
import com.vac.manager.model.generic.exceptions.NotImplementedException
import com.vac.manager.model.team.Team
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.IncorrectDateException
import javax.management.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IncorrectNameException
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

@RunWith(classOf[SpringJUnit4ClassRunner]) //@ContextConfiguration(locations = Array("classpath:/application.xml", "classpath:/spring-config-test.xml"))
@SpringApplicationConfiguration(classes = Array(classOf[Application]), locations = Array("classpath:/spring-config-test.xml"))
@Transactional
class CompetitionDaoJpaIntegrationTest {

  @Autowired
  private var competitionService: CompetitionService = _

  @PersistenceContext
  private var entityManager: EntityManager = _

  @Test
  def createAndFindById() = {
    val fedId: Long = 1
    val competitionName = "Pepitos Volantes"
    val slug = "pepitos"

    var competition = competitionService.createCompetition(fedId, competitionName, slug)
    entityManager.flush()

    assertEquals(fedId, competition.fedId)
    assertEquals(competitionName, competition.competitionName)
    assertEquals(slug, competition.slug)

    val l = entityManager.createQuery("SELECT l FROM Competition l", classOf[Competition]).getResultList()
    println("XXX GOT VAL " + l)

    var secondCompetition = competitionService.findBySlug(fedId, slug)

    assertNotNull(secondCompetition)
    assertFalse(secondCompetition.isEmpty)

    secondCompetition match {
      case None => fail()
      case Some(aCompetition) => assertEquals(competition, aCompetition)
    }

    assertNotNull(secondCompetition)

  }
}

