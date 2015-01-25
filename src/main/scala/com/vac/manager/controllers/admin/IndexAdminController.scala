package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.util.Layout
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class Link(_url: String, _name: String) {
  @BeanProperty
  val url = _url

  @BeanProperty
  val name = _name
}

@Controller
@Layout("layouts/default_admin")
class IndexAdminController extends UrlGrabber {

  def index() = {
    new ModelAndView("admin/index").addObject("links", List(
      new Link(getUrl("LeagueAdminController.list"), "List/edit admin leagues")
    ).asJava)
  }
}
