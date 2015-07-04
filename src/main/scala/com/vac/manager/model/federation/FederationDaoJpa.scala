package com.vac.manager.model.federation.daojpa

import javax.inject.Named

import com.vac.manager.model.federation._
import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.generic.GenericDaoJPADomain
import javax.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import scala.collection.JavaConverters._
import scala.util.Try

@Entity
@Table(name = "federation")
class Federations {
  @Id
  @Column(name = "FEDID")
  @SequenceGenerator(name = "fedIdGenerator", sequenceName = "fed_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "fedIdGenerator")
  @BeanProperty
  var fedId: java.lang.Long = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var fedName: String = _

  @Transient
  def toDomain(): Federation = {
    Federation(Option(fedId), fedName)
  }

  override def equals(obj: Any): Boolean = {
    Try(obj.asInstanceOf[Federations]).toOption.exists(other =>
      fedId.equals(other.fedId) &&
        fedName.equals(other.fedName))
  }

}

@Entity
@Table(name = "federation_domain_name")
class FederationDomainNames {

  @Id
  @Column
  @BeanProperty
  var dns: String = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "FEDID", nullable = false)
  var fed: Federations = _

  @Transient
  def toDomain(): FederationDomainName = {
    FederationDomainName(dns, fed.fedId)
  }
}

@Named("federationDaoJpaBackup")
class FederationDaoJpa extends GenericDaoJPADomain[Federation, Federations, Long](classOf[Federations]) with FederationDao {

  implicit def dnsFromDomain(dns: FederationDomainName): FederationDomainNames = {
    val r = new FederationDomainNames
    r.dns = dns.dns
    r.fed = findById(dns.fedId).get
    r
  }

  override def fromDomain(f: Federation): Federations = {
    val r = new Federations
    r.fedId = f.fedId.map(_.asInstanceOf[java.lang.Long]).orNull
    r.fedName = f.fedName
    r
  }

  override def toDomain(entity: Federations): Federation = entity.toDomain
  implicit def dnsToDomain(dns: FederationDomainNames): FederationDomainName = dns.toDomain

  override def findById(fedId: Long) = {
    Option(entityManager.find(entityClass, fedId.asInstanceOf[java.lang.Long]))
  }

  def findByName(fedName: String): Option[Federation] = {
    val fed = getEntityManager.createQuery(
      "SELECT f FROM Federations f " +
        "WHERE f.fedName = :fedName", classOf[Federations]
    )
      .setParameter("fedName", fedName)
      .getResultList

    return if (fed.isEmpty) {
      None
    } else {
      Some(fed.get(0))
    }
  }

  def findByDomainName(dns: String): Option[Federation] = {
    val fed = getEntityManager.createQuery(
      "SELECT dns.fed FROM FederationDomainNames dns " +
        "WHERE dns.dns = :servername ", classOf[Federations]
    )
      .setParameter("servername", dns)
      .getResultList

    return if (fed.isEmpty) {
      None
    } else {
      Some(fed.get(0))
    }
  }

  def findDomainNames(fedId: Long): Seq[String] = {
    val dns = entityManager.createQuery("SELECT dns.dns FROM FederationDomainNames dns " +
      "WHERE dns.fed.fedId = :fedId ", classOf[String])
      .setParameter("fedId", fedId.asInstanceOf[java.lang.Long])
      .getResultList

    return dns.asScala
  }

  def findDomainNamesAsEntity(fedId: Long): Seq[FederationDomainName] = {
    val dns = entityManager.createQuery("SELECT dns FROM FederationDomainNames dns " +
      "WHERE dns.fed.fedId = :fedId ", classOf[FederationDomainNames])
      .setParameter("fedId", fedId.asInstanceOf[java.lang.Long])
      .getResultList

    return dns.asScala.map(_.toDomain)
  }

  def saveDomainName(entity: FederationDomainName) = {
    // Implicit conversion does not work with persist/remove
    entityManager persist (dnsFromDomain(entity))
  }

  def removeDomainName(entity: FederationDomainName) = {
    entityManager remove (dnsFromDomain(entity))
  }
}

