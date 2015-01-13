package com.vac.manager.controllers.int.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.util.Layout
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import scala.collection.JavaConverters._

@Controller
@Layout("layouts/default_int")
class IntAdminIndexController extends UrlGrabber {

  def index(): ModelAndView = {

    val links = Map("federations" -> getUrl("FederationCRUDController.list"))

    new ModelAndView("int/admin/index")
      .addObject("links", links.asJava.entrySet())
  }
}

