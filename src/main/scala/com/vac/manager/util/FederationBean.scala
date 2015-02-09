package com.vac.manager.util

import javax.servlet.ServletRequest
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.federation.FederationDao

trait FederationBean {
  def getId(): java.lang.Long
}

class FederationBeanImpl(req: ServletRequest) extends FederationBean {

  @Autowired
  var federationDao: FederationDao = _ // Future FederationDao
  var commonNames = List("127.0.0.1", "localhost", "localhost.localdomain")

  lazy val id: java.lang.Long = {
    val serverInAttribute = (req getAttribute "com.vac.manager.request.domain").asInstanceOf[String]
    val serverName = req.getServerName

    val fedDomain = if (commonNames contains serverName) {
      (Option(serverInAttribute) orElse Option(serverName)).get
    } else {
      serverName
    }

    // Go by other rules in development mode
    if (fedDomain == "127.0.0.1" && req.getLocalAddr() == "127.0.0.1") {
      println("FEDERATIONBEAN REQ IS LOCALLY-TRIGGERED")
      1L
    } else {

      println("FEDERATIONBEAN REQ LOCALADDR = " + req.getLocalAddr())
      println("FEDERATIONBEAN REQ SERVERNAME = " + req.getServerName())
      println("FEDERATIONBEAN FEDDOMAIN = " + fedDomain)

      val fed = federationDao findByDomainName fedDomain

      fed match {
        case None => throw new RuntimeException("No Federation registered" +
            "at this URL in a controller depending on federation-related elements")
        case Some(f) => f.fedId
      }
    }
  }

  def getId(): java.lang.Long = { return id }

}
