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

@Controller
@RequestMapping(value=Array("/staff"))
class StaffController {
  
  @Autowired
  var staffService: StaffService = _

  @Autowired
  var playerService: PlayerService = _
    
  @RequestMapping(value=Array("/"), method=Array(RequestMethod.GET))
  def showStaffList() = {
    ""
  }
  
  @RequestMapping(value=Array("/{staffId}"), method=Array(RequestMethod.GET))
  def showStaff() = {
    ""
  }

}




