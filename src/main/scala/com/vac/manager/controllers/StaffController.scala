package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.staff.StaffMemberService
import com.vac.manager.model.staff.StaffMember
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

@Controller
class StaffController extends UrlGrabber {

  @Autowired
  var staffMemberService: StaffMemberService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var federationService: FederationService = _

  @Autowired
  var federation: FederationBean = _

  class ActionableStaff(staff: StaffMember)
    extends StaffMember()
    with UrlGrabber {

    staffName = staff.staffName
    staffSurnames = staff.staffSurnames
    staffActivated = staff.staffActivated
    staffAlias = staff.staffAlias
    staffEmail = staff.staffEmail
    staffAvatarLink = staff.staffAvatarLink
    staffTelephones = staff.staffTelephones
    staffAddress = staff.staffAddress
    staffCardId = staff.staffCardId
    staffBirth = staff.staffBirth
    staffTeamList = staff.staffTeamList
    staffFederation = staff.staffFederation

    def getShowLink(): String = {
      getUrl("StaffController.showStaff", "staffId" -> staff.staffId)
    }

    def getEditLink(): String = {
      getUrl("StaffController.edit", "staffId" -> staff.staffId)
    }

    def getRemoveLink: String = ""
    def getAssignTeamLink: String = ""
    def getEditPrivacyLink: String = ""
  }

  class FindStaffs() {

    @BeanProperty
    var byName: String = null

    @BeanProperty
    var byCardId: String = null

    @BeanProperty
    var byEmail: String = null

    @BeanProperty
    var byActivated: Boolean = false

    @BeanProperty
    var activatedValue: Boolean = false

    @BeanProperty
    var byAllStaff: Boolean = false

  }

  def find(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receiver
    val receiverFind = new FindStaffs

    // Submit params
    val submitUrl: String = getUrl("StaffController.list")
    val submitMethod: String = "POST"

    new ModelAndView("staff/find")
      .addObject("receiver", receiverFind)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet())
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def list(
    @RequestParam byName: String,
    @RequestParam byCardId: String,
    @RequestParam byEmail: String,
    @RequestParam byActivated: Boolean,
    @RequestParam activatedValue: Boolean,
    @RequestParam byAllStaff: Boolean): ModelAndView = {

    // TODO: Check errors
    // TODO: Check if none parameter is activated
    val fedId: Long = federation.getId
    val createLink: String = getUrl("StaffController.create")
    val findLink: String = getUrl("StaffController.find")

    val startIndex: Int = 0
    val count: Int = 10

    var staffList: Seq[ActionableStaff] = Nil

    if (byName.nonEmpty)
      staffList = staffMemberService.findByName(byName,
        startIndex, count).map(new ActionableStaff(_))

    if (byCardId.nonEmpty)
      staffList = staffMemberService.findByCardId(byCardId,
        startIndex, count).map(new ActionableStaff(_))

    if (byEmail.nonEmpty)
      staffList = staffMemberService.findByEmail(byEmail,
        startIndex, count).map(new ActionableStaff(_))

    if (byActivated)
      staffList = staffMemberService.findAllByActivated(activatedValue,
        startIndex, count).map(new ActionableStaff(_))

    if (byAllStaff)
      staffList = staffMemberService.findAllByFederationId(fedId) map (new ActionableStaff(_))

    new ModelAndView("staff/list")
      .addObject("createLink", createLink)
      .addObject("findLink", findLink)
      .addObject("staffList", staffList.asJava)
  }

  def showStaff(
    @PathVariable("staffId") staffId: Long): ModelAndView = {

    val fedId: Long = federation.getId

    val maybeStaff: Option[StaffMember] = staffMemberService.find(staffId)

    maybeStaff match {
      case None => new ModelAndView("staff/notfound")
      case Some(staff) => {
        return new ModelAndView("staff/show")
          .addObject("staff", new ActionableStaff(staff))
      }
    }
  }

  def create(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receivers
    val receiverStaff = new StaffMember()
    val receiverAddress = new Address()

    // Submit params
    val submitUrl: String = getUrl("StaffController.createPost")
    val submitMethod: String = "POST"

    new ModelAndView("staff/edit")
      .addObject("action", "Create")
      .addObject("address", receiverAddress)
      .addObject("staff", receiverStaff)
      .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
      .addObject("submitUrl", submitUrl)
      .addObject("submitMethod", submitMethod)
  }

  def createPost(
    address: Address,
    staffReceiver: StaffMember,
    result: BindingResult): ModelAndView = {

    /*
    if (result.hasErrors()) {
      return new ModelAndView("staff/edit")
        .addObject("staff", staffReceiver)
    }
	*/
    val fedId: Long = federation.getId

    try {

      // Save new staff
      val staffMember: StaffMember =
        staffMemberService.createStaff(
          staffReceiver.staffName,
          staffReceiver.staffSurnames,
          staffReceiver.staffEmail,
          staffReceiver.staffTelephones,
          staffReceiver.staffCardId,
          staffReceiver.staffBirth,
          fedId)

      // Create address
      val staffAddress = new Address(
        address.firstLine, address.secondLine, address.postCode,
        address.locality, address.province, address.country)

      // Assign address to created staff
      val staffAssigned: Option[StaffMember] = staffMemberService.assignAddress(staffMember.staffId, staffAddress)

      new ModelAndView("redirect:" + getUrl("StaffController.showStaff", "staffId" -> staffMember.staffId))
    } catch {

      // Federation not found
      case e: InstanceNotFoundException => return new ModelAndView("federation/notfound")
    }
  }

  def edit(
    @RequestParam staffId: java.lang.Long): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    // Receivers
    var receiverAddress = new Address()

    // Submit params
    val submitUrl: String = getUrl("StaffController.editPost", "staffId" -> staffId)
    val submitMethod: String = "POST"

    val maybeStaffMember: Option[StaffMember] = staffMemberService.find(staffId)

    maybeStaffMember match {
      case None => new ModelAndView("staff/notfound")
      case Some(staffMember) => {
        receiverAddress = staffMember.staffAddress

        new ModelAndView("staff/edit")
          .addObject("action", "Edit")
          .addObject("address", receiverAddress)
          .addObject("staff", staffMember)
          .addObject("hiddens", Map("fedId" -> fedId).asJava.entrySet)
          .addObject("submitUrl", submitUrl)
          .addObject("submitMethod", submitMethod)
      }
    }
  }

  def editPost(
    @RequestParam staffId: java.lang.Long,
    address: Address,
    staff: StaffMember,
    result: BindingResult): ModelAndView = {

    /*
    if (result.hasErrors()) {
      return new ModelAndView("staff/edit")
        .addObject("staff", staff)
    }
	*/

    val fedId: Long = federation.getId

    // Modify Staff
    val modifiedStaffMember: Option[StaffMember] =
      staffMemberService.modifyStaff(
        staffId,
        staff.staffName,
        staff.staffSurnames,
        staff.staffEmail,
        staff.staffTelephones,
        address,
        staff.staffCardId,
        staff.staffBirth)

    modifiedStaffMember match {
      case None => new ModelAndView("staff/notfound")
      case Some(stMember) =>
        new ModelAndView(
          "redirect:" + getUrl("StaffController.showStaff", "staffId" -> stMember.staffId))
    }
  }
}




