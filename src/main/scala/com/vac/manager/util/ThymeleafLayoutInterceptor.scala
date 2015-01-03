package com.vac.manager.util

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

class ThymeleafLayoutInterceptor extends HandlerInterceptorAdapter {
  private final val DEFAULT_LAYOUT = Layout.DEFAULT_LAYOUT
  private final val DEFAULT_VIEW_ATTRIBUTE_NAME = "view"

  val defaultLayout = DEFAULT_LAYOUT
  val viewAttributeName = DEFAULT_VIEW_ATTRIBUTE_NAME

  override def postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object, mav: ModelAndView): Unit = {

    if (mav == null || !mav.hasView())
      return

    val originalViewName = mav.getViewName()

    if (isRedirectOrForward(originalViewName))
      return

    val layoutName = getLayoutName(handler)

    if (layoutName == null)
      return

    mav.setViewName(layoutName)
    mav.addObject(viewAttributeName, originalViewName)
  }

  private def isRedirectOrForward(viewName: String): Boolean = {
    return viewName.startsWith("redirect:") || viewName.startsWith("forward:") || viewName.startsWith("error")
  }

  private def getLayoutName(handler: Object): String = {
    val handlerMethod = handler.asInstanceOf[HandlerMethod]
    val layout: Layout = getMethodOrTypeAnnotation(handlerMethod)

    if (layout == null)
      defaultLayout
    else
      layout.value();
  }

  private def getMethodOrTypeAnnotation(handlerMethod: HandlerMethod): Layout = {
    val layout = handlerMethod.getMethodAnnotation(classOf[Layout])
    if (layout == null) {
      handlerMethod.getBeanType().getAnnotation(classOf[Layout])
    }
    layout
  }

}
