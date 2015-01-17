package com.vac.manager.service.competition

import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.mockito.Mockito
import org.mockito.Mockito.{ when => whenMock, _ }
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.PropertyChecks._
import com.vac.manager.model.competition._

/*
   This is a stub test class. To learn how to customize it,
see the documentation for `ensime-goto-test-configs'
*/

class LeagueServiceImplTest extends FeatureSpec with GivenWhenThen with MockitoSugar with Matchers {
  feature("League creation") {
    scenario("Leagues can be created on a federation without leagues") {
      Given("A federation")
      val fedId = 1

      val service = new LeagueServiceImpl
      service.leagueDao = mock[LeagueDao]
      service.leagueSeasonDao = mock[LeagueSeasonDao]

      When("A league for that federation is created with name and slug")

      Then("The league gets created")
      Then("The league has the correct name")
      Then("The league has the correct url")
      forAll("name", "slug") { (name: String, slug: String) =>
        Mockito.when(service.leagueDao.findBySlug(fedId, slug)).thenReturn(None)

        val l = service.createLeague(fedId, name, slug)

        l.leagueName == name &&
          l.slug == slug &&
          service.findBySlug(fedId, slug).isDefined == true
      }
      Then("The url exists")
    }

    scenario("The same league cannot be created twice in the same federation") {
      Given("A federation")
      val fedId = 1

      val service = new LeagueServiceImpl
      service.leagueDao = mock[LeagueDao]
      service.leagueSeasonDao = mock[LeagueSeasonDao]

      When("A league is created for that federation")
      Then("It cannot be created again")

      forAll("name", "slug") { (name: String, slug: String) =>
        val l = service.createLeague(fedId, name, slug)

        Mockito.when(service.leagueDao.findBySlug(fedId, slug)).thenReturn(Some(l))

        intercept[DuplicateInstanceException] {
          service.createLeague(fedId, name, slug)
        }
      }
    }
  }
}
