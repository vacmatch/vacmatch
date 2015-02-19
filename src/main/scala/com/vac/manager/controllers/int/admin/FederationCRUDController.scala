package com.vac.manager.controllers.int.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.federation.Federation
import com.vac.manager.model.federation.FederationDao
import com.vac.manager.service.federation.FederationService
import com.vac.manager.auth.model.UserAuthService
import com.vac.manager.util.Layout
import com.vac.manager.util.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Controller
@Layout("layouts/default_int")
class FederationCRUDController extends UrlGrabber {

  class FederationCRUD(base: Federation, dns: String) extends Federation {
    fedId = base.fedId
    fedName = base.fedName

    def getEditLink(): String = getUrl("FederationCRUDController.edit", "fedId" -> fedId)
    def getUserLink(): String = dns
    def getDeleteLink(): String = getUrl("FederationCRUDController.delete", "fedId" -> fedId)
    def getManageLink(): String = getUrl("FederationMgmtController.show", "fedId" -> fedId)

    def getCreateRolesLink(): String = getUrl("FederationCRUDController.createDefaultRoles", "fedId" -> fedId)
  }

  @Autowired
  var federationService: FederationService = _

  @Autowired
  var userAuthService: UserAuthService = _

  def list(@ModelAttribute("pageable") p: Pageable): ModelAndView = {

    val pageable =
      if (p == null) {
        val p1 = new Pageable
        p1.start = 0

        p1
      } else p

    val raw_federations = federationService.findAll(pageable)

    val federations: Seq[FederationCRUD] = raw_federations.map(fed => {
      val dnslist = federationService findDomainNames fed.fedId
      val dns = if (dnslist.size > 0) dnslist(0) else "#"

      new FederationCRUD(fed, dns)
    })

    return new ModelAndView("int/admin/federation/list")
      .addObject("federations", federations.asJava)
      .addObject("createUrl", getUrl("FederationCRUDController.create"))
  }

  def create(): ModelAndView = {
    val fed = new Federation()

    return new ModelAndView("int/admin/federation/edit_form")
      .addObject("submitUrl", getUrl("FederationCRUDController.createPost"))
      .addObject("entity", fed)
      .addObject("action", "create")
      .addObject("hiddens", List().asJava)
      .addObject("submitMethod", "POST")
  }

  def edit(@RequestParam("fedId") fedId: java.lang.Long): ModelAndView = {
    val fed = federationService.find(fedId)
    val domains = federationService.findDomainNames(fedId).reduce { (a, b) => a + "\n" + b }

    return new ModelAndView("int/admin/federation/edit_form")
      .addObject("submitUrl", getUrl("FederationCRUDController.editPost"))
      .addObject("entity", fed.orNull)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
      .addObject("dns", domains)
      .addObject("action", "edit")
      .addObject("submitMethod", "POST")
  }

  def createPost(
    @RequestParam("fedName") fedName: String,
    @RequestParam("dns") dnsTextArea: String
  ): String = {
    val domains = dnsTextArea.split("\n").map(_.trim).filter(_.nonEmpty)

    federationService.createFederation(fedName, domains)
    federationService.findByName(fedName).map(f => createDefaultRoles(f.fedId))

    return "redirect:" + getUrl("FederationCRUDController.list")
  }

  def editPost(
    @ModelAttribute fed: Federation,
    @RequestParam("dns") dnsTextArea: String
  ): String = {
    federationService.modifyFederationName(fed.fedId, fed.fedName)

    val prevDomains = federationService.findDomainNames(fed.fedId)
    val curDomains = dnsTextArea.split("\n").map(_.trim).filter(_.nonEmpty)

    // Add = x in curr / x not in prev
    val toAdd = curDomains.filterNot { prevDomains.contains(_) }

    // Del = x in prev / x not in curr
    val toDel = prevDomains.filterNot { curDomains.contains(_) }

    val added = toAdd.map { dns => (dns, federationService.addFederationDomain(fed.fedId, dns)) }
    val removed = toDel.map { dns => (dns, federationService.removeFederationDomain(fed.fedId, dns)) }

    val tra = added.map {
      case (dns, state) =>
        if (state)
          Right(dns + " was added successfully")
        else
          Left(dns + " could not be added")
    }

    val trr = removed.map {
      case (dns, state) =>
        if (state)
          Right(dns + " was removed successfully")
        else
          Left(dns + " could not be removed")
    }
    val xs = tra ++ trr

    val info = xs.collect { case Right(x) => x }
    val err = xs.collect { case Left(x) => x }

    // TODO improve this with a better UrlGrabber

    return "redirect:" + getUrl(
      "FederationCRUDController.list",
      "info" -> info.toList.asJava,
      "err" -> err.toList.asJava
    )
  }

  /**
   * Federations cannot yet create their own roles, but we can force
   * sane defaults on a customized system
   */
  def createDefaultRoles(@RequestParam("fedId") fedId: Long) = {
    userAuthService.createRole(fedId, "ADMINFED", "Federation administrator")
    userAuthService.createRole(fedId, "REFEREE", "Referee")

    "redirect:" + getUrl("UserAdminController.list", "info" -> "Roles created")
  }

}

