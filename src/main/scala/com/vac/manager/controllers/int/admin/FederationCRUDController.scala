package com.vac.manager.controllers.int.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.model.federation.Federation
import com.vac.manager.model.federation.FederationDao
import com.vac.manager.service.federation.FederationService
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

    def getEditLink(): String = {
      return getUrl("FederationCRUDController.edit", "fedId" -> fedId)
    }

    def getUserLink(): String = {
      return dns
    }

    def getDeleteLink(): String = {
      return getUrl("FederationCRUDController.delete", "fedId" -> fedId)
    }

    def getManageLink(): String = {
      return getUrl("FederationMgmtController.show", "fedId" -> fedId)
    }
  }

  @Autowired
  var federationService: FederationService = _

  def list(@ModelAttribute("pageable") pageable: Pageable): ModelAndView = {

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
    val fed = federationService find fedId

    return new ModelAndView("int/admin/federation/edit_form")
      .addObject("submitUrl", getUrl("FederationCRUDController.editPost"))
      .addObject("entity", fed)
      .addObject("hiddens", Map("fedId" -> fedId))
      .addObject("action", "edit")
      .addObject("submitMethod", "POST")
  }

  def createPost(@ModelAttribute fed:Federation): String = {
    return editPost(fed)
  }

  def editPost(@ModelAttribute fed: Federation): String = {
    federationService modifyFederationName(fed.fedId, fed.fedName)

    return "redirect:" + getUrl("FederationCRUDController.list")
  }

}

