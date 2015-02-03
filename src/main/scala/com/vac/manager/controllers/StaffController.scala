package com.vac.manager.controllers.staff

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.service.staff.StaffService
import com.vac.manager.model.staff.Staff
import org.springframework.web.servlet.ModelAndView
import com.vac.manager.model.staff.Player
import com.vac.manager.service.staff.PlayerService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.annotation.ModelAttribute
import com.vac.manager.service.personal.AddressService
import com.vac.manager.model.personal.Address
import java.util.Calendar
import javax.servlet.http.HttpSession
import com.vac.manager.controllers.utils.UrlGrabber
import javax.validation.Valid
import org.springframework.validation.BindingResult
import com.vac.manager.model.personal.AddressSpain
import com.vac.manager.service.personal.AddressSpainService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import scala.beans.BeanProperty

@Controller
@SessionAttributes(Array("fedId"))
class StaffController extends UrlGrabber {
  
  @Autowired
  var staffService: StaffService = _

  @Autowired
  var addressSpainService: AddressSpainService = _
  
  class PostStaff(fedId: Long) {
    
    var federationId: Long = fedId
    @BeanProperty
    var stName: String = _
    @BeanProperty
    var stSurnames: String = _
    @BeanProperty
    var stEmail: String = _
    @BeanProperty
    var stTelephones: String = _
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
    @BeanProperty
    var stNif: String = _
    @BeanProperty
    var stBirth: Calendar = _
    
    def getShowLink: String = ""
    def getEditLink: String = ""
    def getRemoveLink: String = ""
    def getAssignTeamLink: String = ""
    def getModifyPrivacityLink: String = ""
  }

  def list(
      @RequestParam("fedId") fedId: Long,
      session: HttpSession) = {
    
    //Get fedId from session
    //var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var staffList: Seq[Staff] = staffService.findAllByFederationId(fedId)
    
    var mav: ModelAndView = new ModelAndView("staff/list")
    mav.addObject("staffList", staffList)
    mav
  }
  
  def showStaff(
      @PathVariable("staffId") staffId: Long, 
      @RequestParam("fedId") fedId: Long,
      session: HttpSession)= {
    
    //Get fedId from session
    //var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var staffOpt: Option[Staff] = staffService.find(staffId)
    
    staffOpt match {
      case None => new ModelAndView("staff/notfound")
      case Some(staff) => { 
	    var mav: ModelAndView = new ModelAndView("staff/show")
	    mav.addObject("staff", staff)
	    mav
      }
    }
  }
  
  def create(
      @RequestParam("fedId") fedId: Long,
      session: HttpSession) = {

    //Get fedId from session
    //var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var submitUrl: String = getUrl("StaffController.createPost")
    var submitMethod: String = "POST"
    var receiverStaff: PostStaff = new PostStaff(fedId)

    var mav: ModelAndView = new ModelAndView("staff/create")
    mav.addObject("staff", receiverStaff)
    mav.addObject("fedId", fedId)
    mav.addObject("submitUrl", submitUrl)
    mav.addObject("submitMethod", submitMethod)
    mav
  }

  def createPost(
      @RequestParam("fedId") fedId: Long,
      @Valid postStaff: PostStaff,
      result: BindingResult) = {

    //Get fedId from session
    //var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var mav: ModelAndView = new ModelAndView("/staff")
    
    if(result.hasErrors()){
      mav = new ModelAndView("staff/new")
    }else{
      var staffSurnames: Seq[String] = staffService.getSurnamesFromString(postStaff.stSurnames)
      var staffTelephones: Seq[String] = staffService.getTelephonesFromString(postStaff.stTelephones)
      var staffAddress: AddressSpain = addressSpainService.createAddress(
          postStaff.addRoad, postStaff.addNumber, postStaff.addFlat,
          postStaff.addPostCode, postStaff.addLocality, postStaff.addProvince,
          postStaff.addCountry)
      
      try {
        var staff: Staff = staffService.createStaff(postStaff.stName, staffSurnames,
          postStaff.stEmail, staffTelephones, staffAddress, postStaff.stNif,
          postStaff.stBirth, fedId)
          
        mav = new ModelAndView("redirect:/staff/" + staff.staffId)
      } catch {
        //Federation not found
        case e: InstanceNotFoundException => mav = new ModelAndView("redirect:/federation/notfound")
      }
    }
    
    mav
  }
}




