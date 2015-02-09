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
    staffPrivacityActivated = staff.staffPrivacityActivated
    staffAlias = staff.staffAlias
    staffEmail = staff.staffEmail
    staffAvatarLink = staff.staffAvatarLink
    staffTelephones = staff.staffTelephones
    staffAddress = staff.staffAddress
    staffNif = staff.staffNif
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

  def list(): ModelAndView = {

    val fedId: Long = federation.getId
    val maybeFed: Option[Federation] = federationService.find(fedId)
    val createLink: String = getUrl("StaffController.create")
    //TODO Handle federation not found

    val staffList: Seq[ActionableStaff] =
      staffMemberService.findAllByFederationId(fedId) map (new ActionableStaff(_))

    val mav: ModelAndView = new ModelAndView("staff/list")
    mav.addObject("createLink", createLink)
    mav.addObject("staffList", staffList.asJava)
    mav
  }

  def showStaff(
    @PathVariable("staffId") staffId: Long): ModelAndView = {

    val fedId: Long = federation.getId

    val maybeStaff: Option[StaffMember] = staffMemberService.find(staffId)
    
    maybeStaff match {
      case None => new ModelAndView("staff/notfound")
      case Some(staff) => {
        val mav: ModelAndView = new ModelAndView("staff/show")
        mav.addObject("staff", new ActionableStaff(staff))
        mav
      }
    }
  }

  def create(): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    //Receivers
    val receiverStaff: StaffMember = new StaffMember()
    val receiverAddress: Address = new Address()
    //Submit params
    val submitUrl: String = getUrl("StaffController.createPost")
    val submitMethod: String = "POST"

    val mav: ModelAndView = new ModelAndView("staff/edit")
    mav.addObject("action", "create")
    mav.addObject("address", receiverAddress)
    mav.addObject("staff", receiverStaff)
    mav.addObject("fedId", fedId)
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", submitMethod)
    mav
  }

  def createPost(
    address: Address,
    staffReceiver: StaffMember,
    result: BindingResult): ModelAndView = {

    if (result.hasErrors()) {
      val mav: ModelAndView = new ModelAndView("staff/edit")
      mav.addObject("staff", staffReceiver)
      mav
    }

    val fedId: Long = federation.getId

    try {
      //Save new staff
      val staffMember: StaffMember = staffMemberService.createStaff(
          staffReceiver.staffName, staffReceiver.staffSurnames, staffReceiver.staffEmail, 
          staffReceiver.staffTelephones, staffReceiver.staffNif, staffReceiver.staffBirth, fedId)

      //Create address
      val staffAddress: Address = new Address(
        address.addressLine, address.postCode,
        address.locality, address.province, address.country)

      //Assign address to created staff
      val staffAssigned: Option[StaffMember] = staffMemberService.assignAddress(staffMember.staffId, staffAddress)

      new ModelAndView("redirect:" + getUrl("StaffController.showStaff", "staffId" -> staffMember.staffId))
    } catch {
      //Federation not found
      case e: InstanceNotFoundException => new ModelAndView("federation/notfound")
    }
  }

  def edit(
    @RequestParam staffId: java.lang.Long): ModelAndView = {

    val fedId: java.lang.Long = federation.getId

    //Receivers
    var receiverAddress: Address = new Address()
    //Submit params
    val submitUrl: String = getUrl("StaffController.editPost", "staffId" -> staffId)
    val submitMethod: String = "POST"

    val maybeStaffMember: Option[StaffMember] = staffMemberService.find(staffId)
    
    maybeStaffMember match {
      case None => new ModelAndView("staff/notfound")
      case Some(staffMember) => {
        receiverAddress = staffMember.staffAddress
        
        val mav: ModelAndView = new ModelAndView("staff/edit")
        mav.addObject("action", "edit")
        mav.addObject("address", receiverAddress)
        mav.addObject("staff", staffMember)
        mav.addObject("fedId", fedId)
        mav.addObject("submitUrl", submitUrl)
        mav.addObject("submitMethod", submitMethod)
        mav
      }
    }
  }

  def editPost(
    @RequestParam staffId: java.lang.Long,
    address: Address,
    staff: StaffMember,
    result: BindingResult): ModelAndView = {

    if (result.hasErrors()) {
      val mav: ModelAndView = new ModelAndView("staff/edit")
      mav.addObject("staff", staff)
      mav
    }

    val fedId: Long = federation.getId
    
    //Modify Staff
    val modifiedStaffMember: Option[StaffMember] =
      staffMemberService.modifyStaff(staffId, staff.staffName,
        staff.staffSurnames, staff.staffEmail, staff.staffTelephones,
        address, staff.staffNif, staff.staffBirth)
    
    modifiedStaffMember match {
      case None => new ModelAndView("staff/notfound")
      case Some(stMember) =>
        val mav: ModelAndView = new ModelAndView(
          "redirect:" + getUrl("StaffController.showStaff", "staffId" -> stMember.staffId))
        mav
    }
  }
}




