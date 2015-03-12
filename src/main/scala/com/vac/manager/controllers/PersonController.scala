package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.staff.PersonService
import com.vac.manager.model.staff.Person
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import com.vac.manager.controllers.utils.UrlGrabber
import javax.validation.Valid
import org.springframework.validation.BindingResult
import com.vac.manager.model.personal.Address
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.personal.AddressService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import scala.beans.BeanProperty
import com.vac.manager.service.federation.FederationService
import scala.collection.JavaConverters._
import org.springframework.web.bind.annotation.ModelAttribute
import com.vac.manager.util.FederationBean
import com.vac.manager.controllers.actionable.ActionablePerson

@Controller
class PersonController extends UrlGrabber {

  @Autowired
  var personService: PersonService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var federationService: FederationService = _

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
    var byActivated: Boolean = false

    @BeanProperty
    var activatedValue: Boolean = false

    @BeanProperty
    var byAllPerson: Boolean = false

  }

  def find(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receiver
    val receiverFind = new FindPersons

    // Submit params
    val submitUrl: String = getUrl("PersonController.list")
    val submitMethod: String = "POST"

    new ModelAndView("person/find")
      .addObject("receiver", receiverFind)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet())
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def list(
    @RequestParam("byName") byName: String,
    @RequestParam("byCardId") byCardId: String,
    @RequestParam("byEmail") byEmail: String,
    @RequestParam("byActivated") byActivated: Boolean,
    @RequestParam("activatedValue") activatedValue: Boolean,
    @RequestParam("byAllPerson") byAllPerson: Boolean): ModelAndView = {

    // TODO: Check errors
    // TODO: Check if none parameter is activated
    val fedId: Long = federation.getId
    val createLink: String = getUrl("PersonController.create")
    val findLink: String = getUrl("PersonController.find")

    val startIndex: Int = 0
    val count: Int = 10

    var personList: Seq[ActionablePerson] = Nil

    if (byName.nonEmpty)
      personList = personService.findByName(byName,
        startIndex, count).map(new ActionablePerson(_))

    if (byCardId.nonEmpty)
      personList = personService.findByCardId(byCardId,
        startIndex, count).map(new ActionablePerson(_))

    if (byEmail.nonEmpty)
      personList = personService.findByEmail(byEmail,
        startIndex, count).map(new ActionablePerson(_))

    if (byActivated)
      personList = personService.findAllByActivated(activatedValue,
        startIndex, count).map(new ActionablePerson(_))

    if (byAllPerson)
      personList = personService.findAllByFederationId(fedId) map (new ActionablePerson(_))

    new ModelAndView("person/list")
      .addObject("createLink", createLink)
      .addObject("findLink", findLink)
      .addObject("personList", personList.asJava)
  }

  def showPerson(
    @PathVariable("personId") personId: Long): ModelAndView = {

    val fedId: Long = federation.getId

    personService.find(personId).map { person =>
      new ModelAndView("person/show")
        .addObject("person", new ActionablePerson(person))
    }.getOrElse(throw new RuntimeException("Person not found"))
  }

  def create(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receivers
    val receiverPerson = new Person()
    val receiverAddress = new Address()
    // Submit params
    val submitUrl: String = getUrl("PersonController.createPost")
    val submitMethod: String = "POST"

    new ModelAndView("person/edit")
      .addObject("action", "Create")
      .addObject("address", receiverAddress)
      .addObject("person", receiverPerson)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def createPost(
    @ModelAttribute("address") address: Address,
    @ModelAttribute("personReceiver") personReceiver: Person,
    result: BindingResult): ModelAndView = {

    /*
    if (result.hasErrors()) {
      return new ModelAndView("person/edit")
        .addObject("person", personReceiver)
    }
    */
    val fedId: Long = federation.getId

    try {
      // Save new person
      val person: Person =
        personService.createPerson(
          personReceiver.name,
          personReceiver.surnames,
          personReceiver.email,
          personReceiver.telephones,
          personReceiver.cardId,
          personReceiver.birth,
          fedId)

      // Create address
      val personAddress = new Address(
        address.firstLine, address.secondLine, address.postCode,
        address.locality, address.province, address.country)

      // Assign address to created person
      val personAssigned: Option[Person] = personService.assignAddress(person.personId, personAddress)

      new ModelAndView("redirect:" + getUrl("PersonController.showPerson", "personId" -> person.personId))
    } catch {

      // Federation not found
      case e: InstanceNotFoundException => new ModelAndView("federation/notfound")
    }
  }

  def edit(
    @RequestParam("personId") personId: java.lang.Long): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receivers
    var receiverAddress = new Address()
    // Submit params
    val submitUrl: String = getUrl("PersonController.editPost", "personId" -> personId)
    val submitMethod: String = "POST"

    val maybePersonMember: Option[Person] = personService.find(personId)

    maybePersonMember match {
      case None => new ModelAndView("person/notfound")
      case Some(person) => {
        receiverAddress = person.address

        new ModelAndView("person/edit")
          .addObject("action", "Edit")
          .addObject("address", receiverAddress)
          .addObject("person", person)
          .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
      }
    }
  }

  def editPost(
    @RequestParam("personId") personId: java.lang.Long,
    @ModelAttribute("address") address: Address,
    @ModelAttribute("person") person: Person,
    result: BindingResult): ModelAndView = {

    /*
    if (result.hasErrors()) {
      return new ModelAndView("person/edit")
        .addObject("person", person)
    }
	*/

    val fedId: Long = federation.getId

    // Modify Person
    val modifiedPersonMember: Option[Person] =
      personService.modifyPerson(
        personId,
        person.name,
        person.surnames,
        person.email,
        person.telephones,
        address,
        person.cardId,
        person.birth)

    modifiedPersonMember match {
      case None => new ModelAndView("person/notfound")
      case Some(person) =>
        new ModelAndView(
          "redirect:" + getUrl("PersonController.showPerson", "personId" -> person.personId))
    }
  }
}

