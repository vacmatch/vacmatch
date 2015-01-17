package com.vac.manager.util

import com.vac.manager.model.federation.Federation
import com.vac.manager.model.federation.FederationDao
import javax.servlet.ServletRequest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.scalatest.FeatureSpec
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

/*
   This is a stub test class. To learn how to customize it,
see the documentation for `ensime-goto-test-configs'
*/

class FederationBeanImplTest extends FeatureSpec with MockitoSugar with GivenWhenThen {

  feature("FederationBean get federation id") {
    scenario("FederationBean returns 1 on a development environment") {

      Given("A mock servlet with localhost values")
      val req = mock[ServletRequest]
      Mockito.when(req getServerName) thenReturn ("127.0.0.1")
      Mockito.when(req getRemoteHost) thenReturn ("127.0.0.1")
      Mockito.when(req getLocalAddr) thenReturn ("127.0.0.1")

      When("The FederationBean gets instantiated with such a servlet")
      val bean = new FederationBeanImpl(req)
      bean.federationDao = mock[FederationDao]

      Then("it should return 1 as the result")
      Assert.assertEquals(1L: java.lang.Long, bean.getId())

    }

    scenario("FederationBean returns the valid fedId on a production environment") {
      Given("A mock servlet with some valid serverName and remoteHost")
      val req = mock[ServletRequest]
      Mockito.when(req getServerName) thenReturn ("testfed.example")
      Mockito.when(req getRemoteHost) thenReturn ("fe80::48:1234")

      Given("A mock DAO returning a valid federation for such a serverName")
      val federationDao = mock[FederationDao]

      val fed = new Federation
      fed.fedId = 77
      fed.fedName = "TestFed"
      Mockito.when(federationDao findByDomainName "testfed.example") thenReturn (Some(fed))

      When("The FederationBean gets instantiated with such a servlet")
      val bean = new FederationBeanImpl(req)
      bean.federationDao = federationDao

      Then("it should return the fedId as the result")
      Assert.assertEquals(fed.fedId, bean.getId())

      Then("The mock should have been called once to find the domain name")
      verify(federationDao) findByDomainName "testfed.example"
    }


    scenario("FederationBean should throw an exception when it cannot find the federation belonging to an environment") {
      Given("A mock servlet with some valid serverName and remoteHost")
      val req = mock[ServletRequest]
      val domain = "producturl.example"
      Mockito.when(req getServerName) thenReturn (domain)
      Mockito.when(req getRemoteHost) thenReturn ("fe80::48:1234")

      Given("A mock DAO returning no federation for such a domain")
      val federationDao = mock[FederationDao]
      Mockito.when(federationDao findByDomainName domain) thenReturn (None)

      When("The FederationBean gets instantiated")
      val bean = new FederationBeanImpl(req)
      bean.federationDao = federationDao

      Then("We should intercept a RuntimeException")
      intercept[RuntimeException] { bean.getId() }
    }
  }
}
