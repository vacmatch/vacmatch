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
import com.vac.manager.service.personal.AddressService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import scala.beans.BeanProperty
import com.vac.manager.service.federation.FederationService
import scala.collection.JavaConverters._

@Controller
class StaffController extends UrlGrabber {
  
  @Autowired
  var staffMemberService: StaffMemberService = _

  @Autowired
  var addressSpainService: AddressService = _

  @Autowired
  var federationService: FederationService = _
  
 // @Autowired
 // var federation: FederationBean = _

  class PostStaff extends StaffMember with UrlGrabber {

    @BeanProperty
    var addRoad: String = _
    @BeanProperty
    var addNumber: String = _
    @BeanProperty
    var addFlat: String = _
    @BeanProperty
    var addPostCode: Int = _
    @BeanProperty
    var addLocality: String = _
    @BeanProperty
    var addProvince: String = _
    @BeanProperty
    var addCountry: String = _
    
    def getShowLink: String = ""
    def getEditLink: String = ""
    def getRemoveLink: String = ""
    def getAssignTeamLink: String = ""
    def getModifyPrivacityLink: String = ""
  }

  def list(
      @RequestParam id: Long): ModelAndView = {
    
    var fedId: Long = id
    
    var staffList: Seq[StaffMember] = staffMemberService.findAllByFederationId(fedId)
    
    var mav: ModelAndView = new ModelAndView("staff/list")
    mav.addObject("staffList", staffList.asJava)
    mav
  }
  
  def showStaff(
      @RequestParam id: Long,
      @PathVariable("staffId") staffId: Long): ModelAndView = {
    
    var fedId: Long = id
    
    var maybeStaff: Option[StaffMember] = staffMemberService.find(staffId)
    
    maybeStaff match {
      case None => new ModelAndView("staff/notfound")
      case Some(staff) => { 
	    var mav: ModelAndView = new ModelAndView("staff/show")
	    mav.addObject("staff", staff)
	    mav
      }
    }
  }
  
  def create(
      @RequestParam id: Long): ModelAndView = {

    var fedId: Long = id
    var receiverStaff: PostStaff = new PostStaff

    var submitUrl: String = getUrl("StaffController.createPost")
    var submitMethod: String = "POST"

    val mav: ModelAndView = new ModelAndView("staff/create")
    mav.addObject("staff", receiverStaff)
    mav.addObject("fedId", fedId)
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", submitMethod)
    mav
  }

  def createPost(
      @RequestParam id: Long,
      @Valid postStaff: PostStaff,
      result: BindingResult): ModelAndView = {

    var fedId: Long = id

    if(result.hasErrors())
      new ModelAndView("staff/new")
    
    var staffAddress: Address = addressSpainService.createAddress(
      postStaff.addRoad, postStaff.addNumber, postStaff.addFlat,
      postStaff.addPostCode, postStaff.addLocality, postStaff.addProvince,
      postStaff.addCountry)
      
    try {
      var staff: StaffMember = staffMemberService.createStaff(postStaff.staffName, postStaff.staffSurnames,
        postStaff.staffEmail, postStaff.staffTelephones, staffAddress, postStaff.staffNif,
        postStaff.staffBirth, fedId)

      new ModelAndView("redirect:/staff/" + staff.staffId)
    } catch {
        //Federation not found
        case e: InstanceNotFoundException => new ModelAndView("redirect:/federation/notfound")
    }
  }
}




