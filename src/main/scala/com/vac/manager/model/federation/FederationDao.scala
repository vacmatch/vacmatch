package com.vac.manager.model.federation

import com.vac.manager.model.generic.GenericDao

trait FederationDao extends GenericDao[Federation, Long] {
  def findByName(fedName: String): Option[Federation]
  def findByDomainName(dns: String): Option[Federation]
  def findDomainNames(fedId: Long): Seq[String]
  def saveDomainName(entity: FederationDomainName): Unit

  def findDomainNamesAsEntity(fedId: Long): Seq[FederationDomainName]
  def removeDomainName(entity: FederationDomainName)
}
