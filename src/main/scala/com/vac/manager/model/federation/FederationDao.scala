package com.vac.manager.model.federation

import com.vac.manager.model.generic.GenericDao

trait FederationDao extends GenericDao[Federation, java.lang.Long] {
  def findByDomainName(dns: String): Option[Federation]
  def findDomainNames(fedId: java.lang.Long): Seq[String]
}
