package com.vac.manager.util

import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TenantFilter() extends Filter {

  val logger: Logger = LoggerFactory.getLogger(classOf[TenantFilter])

  @throws[ServletException]()
  override def init(config: FilterConfig) = {
    logger.info("Initializing the TenantFilter")
  }

  @throws[ServletException]()
  @throws[IOException]()
  override def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val r = req.asInstanceOf[HttpServletRequest]
    var uri = r.getPathInfo()
    if (uri == null)
      uri = r.getServletPath()

    logger.trace("Got TenantFilter request")
    logger.debug("TenantFilter original request URL = " + uri)

    if (uri.startsWith("/a/")) {
      val mostlyAll = uri.substring("/a/".length())
      val fedName = mostlyAll.substring(0, mostlyAll.indexOf("/", 0))
      val rest = mostlyAll.substring(fedName.length)

      r.setAttribute("com.vac.manager.request.domain", fedName)
      logger.trace("Set fedName in com.vac.manager.request.domain request attribute")

      var newURI = rest

      val separator = if (newURI.indexOf("?") > -1) {
        "&"
      } else {
        "?"
      }

      newURI = newURI + separator + "fedId=" + fedName

      logger.trace("Successfully crafted newURI = " + newURI)

      r.getRequestDispatcher(newURI).forward(req, res)

    } else {

      logger.debug("Request was not for tenantFilter")
      chain.doFilter(req, res)
    }
  }

  override def destroy() = {}

}
