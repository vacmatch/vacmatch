package com.vac.manager.service.federation

import com.vac.manager.model.federation.{ Federation, FederationDao, FederationDomainName }
import org.mockito.ArgumentMatcher
import org.mockito.Matchers
import org.mockito.{ ArgumentCaptor, Mockito }
import org.mockito.Mockito._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.{ FeatureSpec, GivenWhenThen, PropSpec }
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class FederationServiceImplTest extends PropSpec with MockitoSugar with GeneratorDrivenPropertyChecks {

  def nonEmptyString(s: String) = { s != null && s.trim.nonEmpty }
  def validDns(s: String) = nonEmptyString(s)
  def validFedName(s: String) = nonEmptyString(s)

  val genNonEmptyString = arbitrary[String] suchThat (nonEmptyString(_))
  val genValidDns = arbitrary[String] suchThat (validDns(_))
  val genValidFedId = Gen.choose(1, 1000000000L) // arbitrary[Long] suchThat (_ > 0)

  val validFed = for {
    fedId <- genValidFedId
    fedName <- genNonEmptyString
  } yield {
    val f = new Federation
    f.fedId = fedId
    f.fedName = fedName

    f
  }

  property("federations can be registered for any unregistered name") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]

    forAll((genNonEmptyString, "fedName")) { (fedName: String) =>
      whenever(nonEmptyString(fedName)) {
        val domains = List()

        when(service.federationDao.findByName(fedName)).thenReturn(None)
        when(service.federationDao.findByName(fedName.trim)).thenReturn(None)

        val wasCreated = service.createFederation(fedName, domains)
        assert(wasCreated)
      }
    }
  }

  property("federations cannot be registered with the names of already existing federations") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]

    forAll((validFed, "fed")) { (fed: Federation) =>
      val fedName = fed.fedName
      val domains = List()

      when(service.federationDao.findByName(fedName.trim)).thenReturn(None)
      when(service.federationDao.findByName(fedName)).thenReturn(None)

      val wasCreated = service.createFederation(fedName, domains)

      when(service.federationDao.findByName(fedName.trim)).thenReturn(Some(fed))
      when(service.federationDao.findByName(fedName)).thenReturn(Some(fed))

      val wasCreatedAgain = service.createFederation(fedName, domains)

      assert(wasCreated)
      assert(!wasCreatedAgain)
    }
  }

  property("federations cannot be registered with empty names") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]

    verifyZeroInteractions(service.federationDao)

    assert(service.createFederation("", List()) == false)
    assert(service.createFederation(null, List()) == false)

  }

  property("federations can modify their name once registered") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]

    forAll(
      (genValidFedId, "fedId"),
      (genNonEmptyString, "originalFedName"),
      (genNonEmptyString, "destinyFedName")
    ) {
        (fedId: Long, originalFedName: String, finalFedName: String) =>

          whenever(validFedName(originalFedName) && validFedName(finalFedName)) {
            val domains = List()
            val fed = new Federation
            fed.fedId = fedId
            fed.fedName = originalFedName
            when(service.federationDao.findById(fedId)).thenReturn(fed)

            service.modifyFederationName(fedId, finalFedName)
            verify(service.federationDao).save(fed)

            assert(fed.fedName == finalFedName)
            assert(fed.fedId == fedId)
          }
      }
  }

  property("federations can be added non-clashing domains") {
    val service = new FederationServiceImpl
    val fedId = 1

    forAll(
      (genValidDns, "dnsName"),
      (validFed, "fed")
    ) { (dnsName: String, fed: Federation) =>
        whenever(validDns(dnsName)) {
          service.federationDao = mock[FederationDao]
          when(service.federationDao.findByDomainName(dnsName)).thenReturn(None)
          when(service.federationDao.findById(fed.fedId)).thenReturn(fed)

          service.addFederationDomain(fed.fedId, dnsName)
          val captor = ArgumentCaptor.forClass(classOf[FederationDomainName])

          verify(service.federationDao).saveDomainName(captor.capture())
          assert((captor.getValue.dns) == dnsName)
        }
      }
  }

  property("federation cannot add a clashing domain of another federation") {
    val service = new FederationServiceImpl

    forAll(
      (genNonEmptyString, "domainName"),
      (genValidFedId, "fedId"),
      (genNonEmptyString, "fedName")
    ) { (domainName: String, fedId: Long, fedName: String) =>
        whenever(validDns(domainName) && validFedName(fedName)) {
          service.federationDao = mock[FederationDao]

          val existingFederation = new Federation
          existingFederation setFedId fedId
          existingFederation setFedName fedName
          when(service.federationDao.findByDomainName(domainName)).thenReturn(Some(existingFederation))

          val r = service.addFederationDomain(fedId, domainName)
          val captor = ArgumentCaptor.forClass(classOf[FederationDomainName])

          verify(service.federationDao, never()).saveDomainName(captor.capture())
          assert(r == false)
        }
      }
  }

  property("domains can be removed from federations") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]
  }

  property("federations can be removed when they exist") {
    val service = new FederationServiceImpl
    service.federationDao = mock[FederationDao]

    forAll((validFed, "existingFed")) { (existingFed: Federation) =>
      val fedId = existingFed.fedId
      when(service.federationDao.findById(fedId)).thenReturn(existingFed)

      val r = service.removeFederation(fedId)

      verify(service.federationDao).remove(existingFed)
      assert(r)
    }
  }

  property("federations cannot be removed if they do not exist") {
    val service = new FederationServiceImpl

    forAll((genValidFedId, "fedId")) { (fedId: Long) =>

      // Start with a fresh mock in each iteration so that verify
      // works correctly.
      service.federationDao = mock[FederationDao]
      when(service.federationDao.findById(fedId)).thenReturn(null)

      val r = service.removeFederation(fedId)

      // find is called once, and nothing more (no remove)
      verify(service.federationDao).findById(fedId)
      verifyNoMoreInteractions(service.federationDao)
      assert(r == false)
    }
  }
}

