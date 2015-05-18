package com.vac.manager.controllers.admin

import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.util.FederationBean
import org.springframework.web.servlet.ModelAndView
import com.vac.manager.model.staff.Person
import org.springframework.validation.BindingResult
import com.vac.manager.model.personal.Address
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ModelAttribute
import javax.management.InstanceNotFoundException
import com.vac.manager.controllers.utils.UrlGrabber
import scala.collection.JavaConverters._
import com.vac.manager.service.staff.PersonService
import org.springframework.stereotype.Controller
import com.vac.manager.controllers.actionable.ActionablePerson
import javax.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PathVariable
import scala.beans.BeanProperty

@Controller
class PersonAdminController extends UrlGrabber {

  @Autowired
  var personService: PersonService = _

  @Autowired
  var federation: FederationBean = _

  class FindPersons() {

    @BeanProperty
    var byName: String = _

    @BeanProperty
    var byCardId: String = _

    @BeanProperty
    var byEmail: String = _

    @BeanProperty
    var byAllPerson: Boolean = false

  }

  def find(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receiver
    val receiverFind = new FindPersons

    // Submit params
    val submitUrl: String = getUrl("PersonAdminController.list")
    val submitMethod: String = "POST"

    new ModelAndView("admin/person/find")
      .addObject("receiver", receiverFind)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet())
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def list(
    @RequestParam("byName") byName: String,
    @RequestParam("byCardId") byCardId: String,
    @RequestParam("byEmail") byEmail: String,
    @RequestParam("byAllPerson") byAllPerson: Boolean,
    request: HttpServletRequest
  ): ModelAndView = {

    // TODO: Check errors
    // TODO: Check if none parameter is activated
    val fedId: Long = federation.getId

    // Check user permissions
    val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    val anonymousActionsMenu: Map[String, String] = Map(
      "Find staff" -> getUrl("PersonAdminController.find")
    )
    val authenticatedActionsMenu: Map[String, String] = Map(
      "Create staff" -> getUrl("PersonAdminController.create")
    )
    val actionsMenu = if (userCanEdit) anonymousActionsMenu ++ authenticatedActionsMenu else anonymousActionsMenu

    val startIndex: Int = 0
    val count: Int = 10

    var personList: Seq[ActionablePerson] = Nil

    if (byName.nonEmpty)
      personList = personService.findByName(
        byName,
        startIndex, count
      ).map(new ActionablePerson(_, userCanEdit))

    if (byCardId.nonEmpty)
      personList = personService.findByCardId(
        byCardId,
        startIndex, count
      ).map(new ActionablePerson(_, userCanEdit))

    if (byEmail.nonEmpty)
      personList = personService.findByEmail(
        byEmail,
        startIndex, count
      ).map(new ActionablePerson(_, userCanEdit))

    if (byAllPerson)
      personList = personService.findAllByFederationId(fedId) map (new ActionablePerson(_, userCanEdit))

    new ModelAndView("admin/person/list")
      .addObject("actionsMenu", actionsMenu.asJava)
      .addObject("personList", personList.asJava)
  }

  def listAll(
    request: HttpServletRequest
  ): ModelAndView = {

    // TODO: Check if none parameter is activated
    val fedId: Long = federation.getId

    // Check user permissions
    val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    val anonymousActionsMenu: Map[String, String] = Map(
      "Find staff" -> getUrl("PersonAdminController.find")
    )
    val authenticatedActionsMenu: Map[String, String] = Map(
      "Create staff" -> getUrl("PersonAdminController.create")
    )
    val actionsMenu = if (userCanEdit) anonymousActionsMenu ++ authenticatedActionsMenu else anonymousActionsMenu

    var personList: Seq[ActionablePerson] = personService.findAllByFederationId(fedId) map (new ActionablePerson(_, userCanEdit))

    new ModelAndView("admin/person/list")
      .addObject("actionsMenu", actionsMenu.asJava)
      .addObject("personList", personList.asJava)
  }

  def showPerson(
    @PathVariable("personId") personId: Long,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: Long = federation.getId

    // Check user permissions
    val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    val listLink: String = getUrl("PersonAdminController.listAll")

    personService.find(personId).map { person =>
      new ModelAndView("admin/person/show")
        .addObject("person", new ActionablePerson(person, userCanEdit))
        .addObject("listLink", listLink)
    }.getOrElse(throw new RuntimeException("Person not found"))
  }

  def create(request: HttpServletRequest): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receivers
    val receiverPerson = new Person
    val receiverAddress = new Address()
    // Submit params
    val submitUrl: String = getUrl("PersonAdminController.createPost")
    val submitMethod: String = "POST"

    val listLink: String = getUrl("PersonAdminController.listAll")

    new ModelAndView("admin/person/edit")
      .addObject("action", "Create")
      .addObject("listLink", listLink)
      .addObject("address", receiverAddress)
      .addObject("person", receiverPerson)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def createPost(
    @ModelAttribute("address") address: Address,
    @ModelAttribute("personReceiver") personReceiver: Person,
    result: BindingResult
  ): ModelAndView = {

    val fedId: Long = federation.getId

    try {
      // Save new person
      val person: Person =
        personService.createPerson(
          personReceiver.name,
          personReceiver.surname,
          personReceiver.email,
          personReceiver.telephones,
          personReceiver.cardId,
          personReceiver.birthdate,
          fedId
        )

      // Create address
      val personAddress = new Address(
        address.firstLine, address.secondLine, address.postCode,
        address.locality, address.province, address.country
      )

      // Assign address to created person
      val personAssigned: Option[Person] = personService.assignAddress(person.personId, personAddress)

      new ModelAndView("redirect:" + getUrl("PersonAdminController.showPerson", "personId" -> person.personId))
    } catch {

      // Federation not found
      case e: InstanceNotFoundException => new ModelAndView("federation/notfound")
    }
  }

  def edit(
    @RequestParam("personId") personId: java.lang.Long,
    request: HttpServletRequest
  ): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Check user permissions
    val userCanEdit = request.isUserInRole("ROLE_ADMINFED") || request.isUserInRole("ROLE_ROOT")

    // Receivers
    var receiverAddress = new Address()
    // Submit params
    val submitUrl: String = getUrl("PersonAdminController.editPost", "personId" -> personId)
    val submitMethod: String = "POST"

    personService.find(personId).map {
      person =>
        receiverAddress = person.address

        new ModelAndView("admin/person/edit")
          .addObject("action", "Edit")
          .addObject("address", receiverAddress)
          .addObject("person", new ActionablePerson(person, userCanEdit))
          .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
    }.getOrElse(throw new InstanceNotFoundException("Person not found"))
  }

  def editPost(
    @RequestParam("personId") personId: java.lang.Long,
    @ModelAttribute("address") address: Address,
    @ModelAttribute("person") person: Person
  ): ModelAndView = {

    val fedId: Long = federation.getId

    // Modify Person
    val modifiedPersonMember: Option[Person] =
      personService.modifyPerson(
        personId,
        person.name,
        person.surname,
        person.email,
        person.telephones,
        address,
        person.cardId,
        person.birthdate
      )

    modifiedPersonMember match {
      case None => new ModelAndView("person/notfound")
      case Some(person) =>
        new ModelAndView(
          "redirect:" + getUrl("PersonAdminController.showPerson", "personId" -> person.personId)
        )
    }
  }

}
