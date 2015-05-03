package com.vac.manager.service.federation

import com.vac.manager.model.federation.{ Federation, FederationDao, FederationDomainName }
import com.vac.manager.util.Pageable
import java.lang.Long
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConverters._

@Service("federationService")
@Transactional
class FederationServiceImpl extends FederationService {

  @Autowired
  var federationDao: FederationDao = _

  @Transactional(readOnly = true)
  def find(id: Long): Option[Federation] = {
    return federationDao.findById(id)
  }

  /**
    * Finds elements from pageable.start
    */
  @Transactional(readOnly = true)
  def findAll(pageable: Pageable): Seq[Federation] = {
    return federationDao.findAll.drop(pageable.start)
  }

  @Transactional(readOnly = true)
  def findByName(fedName: String): Option[Federation] = {
    return federationDao.findByName(fedName)
  }

  @Transactional(readOnly = true)
  def findByDomain(domainName: String): Option[Federation] = {
    return federationDao findByDomainName domainName
  }

  @Transactional
  def findDomainNames(fedId: Long): Seq[String] = {
    return federationDao findDomainNames fedId
  }

  /**
    * Creates a new federation with the given domains as valid entry points.
    * In case any domain is already taken registration does not take place.
    */
  @Transactional
  def createFederation(fedName: String, domains: Seq[String]): Boolean = {
    if (fedName == null || fedName.trim == "")
      return false

    if (domains.filter(d => federationDao.findByDomainName(d).isDefined).size > 0) {
      return false
    }

    if (federationDao.findByName(fedName.trim).isDefined) {
      return false
    }

    val fed = _createFederationInTransaction(fedName)

    val registered = domains.filter(domain => _addFederationDomain(fed, domain))

    if (registered.size != domains.size) {
      throw new RuntimeException("One or more domains were not possible to register")
    }

    return true
  }

  @Transactional(propagation = Propagation.NESTED)
  protected def _createFederationInTransaction(fedName: String): Federation = {
    val f = new Federation()
    f.fedName = fedName.trim
    federationDao.save(f)

    return f
  }

  @Transactional
  def removeFederation(fedId: Long): Boolean = {
    find(fedId) match {
      case None => false
      case Some(fed) => {
        // TODO: Perform some checking?
        federationDao remove fed
        true
      }
    }
  }

  @Transactional
  def modifyFederationName(fedId: Long, newName: String): Boolean = {
    find(fedId) match {
      case None => false
      case Some(fed) => {
        fed.fedName = newName
        federationDao save fed

        true
      }
    }
  }

  @Transactional
  def addFederationDomain(fedId: Long, domainName: String): Boolean = {
    find(fedId) match {
      case None => false
      case Some(fed) => {
        return _addFederationDomain(fed, domainName)
      }
    }
  }

  def _addFederationDomain(fed: Federation, domainName: String): Boolean = {
    val existing = federationDao findByDomainName domainName
    if (existing.isDefined)
      return false

    val dns = new FederationDomainName
    dns.fed = fed
    dns.dns = domainName

    federationDao saveDomainName dns
    return true
  }

  def removeFederationDomain(fedId: Long, domainName: String): Boolean = {
    val matching = federationDao.findDomainNamesAsEntity(fedId) filter (d => domainName.equals(d.dns))

    if (matching.size > 0) {
      federationDao.removeDomainName(matching(0))
      return true
    } else {
      return false
    }
  }
}
