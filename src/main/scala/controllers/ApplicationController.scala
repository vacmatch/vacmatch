package main.scala.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView

@Controller
class ApplicationController() {

  def index = {
    var mav: ModelAndView= new ModelAndView("home");
    mav
  }
  
}
