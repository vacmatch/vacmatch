package com.vac.manager.util

import javax.servlet.ServletRequest

trait FederationBean {
  def getId (): java.lang.Long
}

class FederationBeanImpl (req: ServletRequest) extends FederationBean {

  // @Autowired
  var federationDao: Option[_] = _ // Future FederationDao

  def getId (): java.lang.Long = {
    val serverName = req.getServerName()

    // Go by other rules in development mode
    if (serverName == "127.0.0.1" && req.getLocalAddr() == "127.0.0.1") {
      println ("FEDERATIONBEAN REQ IS LOCALLY-TRIGGERED")
      return 1
    }

    println ("FEDERATIONBEAN REQ LOCALADDR = " + req.getLocalAddr())
    println ("FEDERATIONBEAN REQ SERVERNAME = " + req.getServerName())
    return 1
  }

}
