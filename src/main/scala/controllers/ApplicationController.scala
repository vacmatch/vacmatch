package main.scala.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(Array("/"))
class ApplicationController() {

  @RequestMapping(Array("/"))
  def index = {
    var mav: ModelAndView= new ModelAndView("home");
    mav
  }
  
}
