package com.vac.manager.model.federation

import com.vac.manager.model.generic.GenericDao

trait FederationDao extends GenericDao[Federation, java.lang.Long] {
  def findByName(fedName: String): Option[Federation]
  def findByDomainName(dns: String): Option[Federation]
  def findDomainNames(fedId: java.lang.Long): Seq[String]
  def saveDomainName(entity: FederationDomainName): Unit

  def findDomainNamesAsEntity(fedId: java.lang.Long): Seq[FederationDomainName]
  def removeDomainName(entity: FederationDomainName)
}
