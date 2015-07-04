package com.vac.manager.model.federation

case class Federation(fedId: Option[Long], fedName: String)
case class FederationDomainName(dns: String, fedId: Long)
