package com.vacmatch.support.springscala

import org.apache.log4j.LogManager
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.async.WebAsyncUtils
import org.springframework.web.method.support.{ HandlerMethodReturnValueHandler, ModelAndViewContainer }
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This class adds configuration support in spring for returning
  * scala.concurrent.Future instances on places such as controllers.
  *
  *
  * @todo Make this a separate sbt project
  * @author Santiago Saavedra <ssaavedra@vacmatch.com>
  */
@Configuration
class ScalaFutureHandlerConfiguration extends WebMvcConfigurerAdapter {
  override def addReturnValueHandlers(returnValueHandlers: java.util.List[HandlerMethodReturnValueHandler]) = {
    returnValueHandlers.add(ScalaFutureMethodValueHandlerWithGlobalExecutionContext)
  }

}

/**
  * This stateless singleton is capable of handling futures given the
  * implicit global execution context for them. If any other execution
  * context is needed, this object is insufficient.
  *
  * @author Santiago Saavedra <ssaavedra@vacmatch.com>
  */
object ScalaFutureMethodValueHandlerWithGlobalExecutionContext extends HandlerMethodReturnValueHandler {
  val log = LogManager.getLogger(getClass)

  override def supportsReturnType(returnType: org.springframework.core.MethodParameter): Boolean = {
    log.warn("Supports return type? " + returnType)
    classOf[scala.concurrent.Future[_]].isAssignableFrom(returnType.getParameterType)
  }

  override def handleReturnValue(
    returnValue: Object,
    returnType: MethodParameter,
    mavContainer: ModelAndViewContainer,
    webRequest: NativeWebRequest
  ) = {
    if (Option(returnValue).isEmpty) {
      mavContainer.setRequestHandled(true)
    } else {
      val dr = new org.springframework.web.context.request.async.DeferredResult[Object]
      WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(dr, mavContainer)

      val t = scala.util.Try(returnValue.asInstanceOf[scala.concurrent.Future[Object]])
        .map { future =>
          future.onSuccess { case o => dr.setResult(o) }
          future.onFailure { case t => dr.setErrorResult(t) }
        }.recover {
          case t: Throwable =>
            log.error("Something weird happened with scala.conc.future handling", t)
        }

      log.warn("SET UP DONE")
    }
  }
}
