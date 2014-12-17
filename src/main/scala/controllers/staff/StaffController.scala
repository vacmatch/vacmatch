package main.scala.controllers.staff

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import main.scala.service.staff.StaffService
import main.scala.model.staff.Staff
import org.springframework.web.servlet.ModelAndView
import main.scala.model.staff.Player
import main.scala.service.staff.PlayerService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.annotation.ModelAttribute
import main.scala.service.personal.AddressService
import main.scala.model.personal.Address
import java.util.Calendar
import javax.servlet.http.HttpSession

@Controller
@SessionAttributes(Array("fedId"))
class StaffController {
  
  @Autowired
  var staffService: StaffService = _

  @Autowired
  var addressService: AddressService = _
  
  def list(
      session: HttpSession) = {
    
    //Get fedId from session
    var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var staffList: Seq[Staff] = staffService.findAllByFederationId(fedId)
    
    var mav: ModelAndView = new ModelAndView("staff/list")
    mav.addObject("staffList", staffList)
    mav
  }
  
  def showStaff(
      @PathVariable("staffId") staffId: Long, 
      session: HttpSession)= {
    
    //Get fedId from session
    var fedId: Long = session.getAttribute("fedId").asInstanceOf[Long]
    
    var staff: Staff = staffService.findByStaffId(staffId, fedId)
    
    var mav: ModelAndView = new ModelAndView("staff/show")
    mav.addObject("staff", staff)
    mav
  }
  
}




