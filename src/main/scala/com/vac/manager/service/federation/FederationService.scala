package com.vac.manager.service.federation

import com.vac.manager.model.federation.{ Federation, FederationDao, FederationDomainName }
import com.vac.manager.util.Pageable
import java.lang.Long

trait FederationService {
  def find(id: Long): Option[Federation]
  def findByName(fedName: String): Option[Federation]
  def findAll(page: Pageable): Seq[Federation]
  def findByDomain(domainName: String): Option[Federation]
  def findDomainNames(fedId: Long): Seq[String]

  def createFederation(fedName: String, domains: Seq[String]): Boolean
  def removeFederation(fedId: Long): Boolean
  def modifyFederationName(fedId: Long, newName: String): Boolean
  def addFederationDomain(fedId: Long, domainName: String): Boolean
  def removeFederationDomain(fedId: Long, domainName: String): Boolean
}

