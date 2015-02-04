package com.vac.manager.controllers.utils

import org.resthub.web.springmvc.router.Router
import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable.{ Map => MuMap }

trait UrlGrabber {
  protected def getUrl(action: String, params: (String, Object)*): String = {
    val jParams = UrlGrabber.getParams(params.toList)
    return Router.getFullUrl(action, jParams) // Map(params:_*).asJava)
  }
}

object UrlGrabber {

  @tailrec
  def getParams(
    params: Seq[(String, Object)],
    map: MuMap[String, List[Object]] = MuMap()
  ): java.util.Map[String, Object] = {
    params match {
      case x :: xs => {
        x match {
          case (k, v0) =>

            val vs: List[Object] = if (v0.isInstanceOf[java.util.Collection[_]])
              v0.asInstanceOf[java.util.Collection[Object]].asScala.toList
            else if (v0.isInstanceOf[List[_]])
              v0.asInstanceOf[List[Object]]
            else if (v0.isInstanceOf[Iterable[_]])
              v0.asInstanceOf[Iterable[Object]].toList
            else
              List(v0)

            map get k match {
              case None => map(k) = vs
              case Some(l) => map(k) = vs ++ l
            }

            getParams(xs, map)
        }
      }

      case Nil => {
        val k: scala.collection.Map[String, Object] =
          map.map {
            case (k, v) =>
              if (v.length == 1) (k, v(0))
              else (k, v.asJava)
          }

        return k.asJava
      }
    }
  }
}

