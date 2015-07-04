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
    federationDao.findById(id)
  }

  /**
    * Finds elements from pageable.start
    */
  @Transactional(readOnly = true)
  def findAll(pageable: Pageable): Seq[Federation] = {
    federationDao.findAll.drop(pageable.start)
  }

  @Transactional(readOnly = true)
  def findByName(fedName: String): Option[Federation] = {
    federationDao.findByName(fedName)
  }

  @Transactional(readOnly = true)
  def findByDomain(domainName: String): Option[Federation] = {
    federationDao findByDomainName domainName
  }

  @Transactional
  def findDomainNames(fedId: Long): Seq[String] = {
    federationDao findDomainNames fedId
  }

  /**
    * Creates a new federation with the given domains as valid entry points.
    * In case any domain is already taken registration does not take place.
    */
  @Transactional
  def createFederation(fedName: String, domains: Seq[String]): Boolean = {
    if (fedName == null || fedName.trim == "")
      return false

    if (domains.count(d => federationDao.findByDomainName(d).isDefined) > 0) {
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

    true
  }

  @Transactional(propagation = Propagation.NESTED)
  protected def _createFederationInTransaction(fedName: String): Federation = {
    val f = Federation(None, fedName.trim)
    federationDao.save(f)
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
        val f = fed.copy(fedName = newName)
        federationDao save f

        true
      }
    }
  }

  @Transactional
  def addFederationDomain(fedId: Long, domainName: String): Boolean = {
    find(fedId)
      .flatMap(_ => federationDao.findByDomainName(domainName)) match {
        case Some(_) => false
        case None => {
          federationDao.saveDomainName(FederationDomainName(domainName, fedId))
          true
        }
      }
  }

  def _addFederationDomain(federation: Federation, domainName: String): Boolean = {
    federation.fedId.exists(fedId =>
      addFederationDomain(fedId, domainName))
  }

  def removeFederationDomain(fedId: Long, domainName: String): Boolean = {
    val matching = federationDao.findDomainNamesAsEntity(fedId) filter (d => domainName.equals(d.dns))

    if (matching.size > 0) {
      federationDao.removeDomainName(matching(0))
      true
    } else {
      false
    }
  }
}
