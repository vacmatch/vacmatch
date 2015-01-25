package com.vac.manager.model.federation

import com.vac.manager.model.generic.GenericDaoJPA
import javax.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import scala.collection.JavaConverters._

@Repository("federationDao")
class FederationDaoJpa extends GenericDaoJPA[Federation, java.lang.Long](classOf[Federation]) with FederationDao {

  def findByDomainName(dns: String): Option[Federation] = {
    val fed = getEntityManager createQuery
      "SELECT dns.fed FROM FederationDomainName dns " +
      "WHERE dns.dns = :servername " setParameter ("servername", dns) getResultList

    return if (fed isEmpty) {
      None
    } else {
      Some((fed get 0).asInstanceOf[Federation])
    }
  }

  def findDomainNames(fedId: java.lang.Long): Seq[String] = {
    val dns = entityManager.createQuery("SELECT dns.dns FROM FederationDomainName dns " +
      "WHERE dns.fed.fedId = :fedId ", classOf[String])
      .setParameter("fedId", fedId)
      .getResultList

    return dns.asScala
  }

  def findDomainNamesAsEntity(fedId:java.lang.Long):Seq[FederationDomainName] = {
    val dns = entityManager.createQuery("SELECT dns FROM FederationDomainName dns " +
      "WHERE dns.fed.fedId = :fedId ", classOf[FederationDomainName])
      .setParameter("fedId", fedId)
      .getResultList

    return dns.asScala
  }

  def saveDomainName(entity:FederationDomainName) = {
    entityManager persist entity
  }

  def removeDomainName(entity:FederationDomainName) = {
    entityManager remove entity
  }
}



