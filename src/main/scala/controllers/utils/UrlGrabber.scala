package main.scala.controllers.utils

import org.resthub.web.springmvc.router.Router
import scala.collection.JavaConverters._

trait UrlGrabber {
  protected def getUrl (action:String, params: (String, Object)*): String = {
    return Router.getFullUrl(action, Map(params:_*).asJava)
  }
}










