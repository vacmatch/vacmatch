package com.vac.manager.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.RequestMapping
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Controller
@RequestMapping(Array("/"))
class ApplicationController() {

  @RequestMapping(Array("/"))
  def index: Future[ModelAndView] = {
    var mav: ModelAndView = new ModelAndView("home");
    Future.successful(mav)
  }

}
