package main.scala.util

import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class TenantFilter() extends Filter {

  @throws[ServletException]()
  override def init(config: FilterConfig) = {
    println ("**** INITIALIZING TENANTFILTER ****")
  }

  @throws[ServletException]()
  @throws[IOException]()
  override def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val r = req.asInstanceOf[HttpServletRequest]
    var uri = r.getPathInfo()
    if (uri == null)
      uri = r.getServletPath()

    println ("***** TENANTFILTER ****")
    println ("uri = " + uri)

    if (uri.startsWith("/a/")) {
      val mostlyAll = uri.substring("/a/".length())
      val fedName = mostlyAll.substring(0, mostlyAll.indexOf("/", 0))
      val rest = mostlyAll.substring(fedName.length)

      r.setAttribute("main.scala.fedName", fedName)

      var newURI = rest

      val separator = if (newURI.indexOf("?") > -1) {
          "&"
      } else {
          "?"
        }

      newURI = newURI + separator + "fedId=" + fedName

      println ("NICE: GOING WITH newURI = " + newURI)

      r.getRequestDispatcher(newURI).forward(req, res)

    } else {

      println ("Damn, not doing stuff")
      chain.doFilter(req, res)
    }
  }

  override def destroy() = {}

}
