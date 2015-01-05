package com.vac.manager

import controllers.conversions.{ CalendarFormatter, DateFormatter }
import java.util.ArrayList
import org.resthub.web.springmvc.router.RouterConfigurationSupport
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.{ FilterRegistrationBean, ServletRegistrationBean }
import org.springframework.context.MessageSource
import org.springframework.context.annotation.{ Bean, ComponentScan, Configuration, Import }
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.format.Formatter
import org.springframework.format.support.FormattingConversionServiceFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.{ InterceptorRegistry, ResourceHandlerRegistry }
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import scala.collection.JavaConverters._
import util.TenantFilter
import util.ThymeleafLayoutInterceptor

@Configuration
@ComponentScan(basePackages = Array("com.vac.manager")) // You should not use the @EnableWebMvc annotation
class WebAppConfig() extends RouterConfigurationSupport {

  override protected def isHandlerMappingReloadEnabled() = { true }

  @Override
  def listRouteFiles(): ArrayList[String] = {
    val routeFiles = new ArrayList[String];
    routeFiles.add("classpath:/routes.conf");
    return routeFiles;
  }

  override def addResourceHandlers(registry: ResourceHandlerRegistry) = {
    if (!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/META-INF/resources", "classpath:/resources/",
          "classpath:/static/", "classpath:/public/")
    }
  }

  @Bean
  def layoutInterceptor: HandlerInterceptorAdapter = {
    return new ThymeleafLayoutInterceptor
  }

  override def addInterceptors(registry: InterceptorRegistry) = {
    registry.addInterceptor(layoutInterceptor)
  }
}

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan @Import(Array(classOf[WebAppConfig]))
class Application {

  @Bean
  def multiTenantHandler(): FilterRegistrationBean = {
    val frb = new FilterRegistrationBean

    frb.addUrlPatterns("/a/*")
    frb.setFilter(new TenantFilter)

    frb
  }

  @Bean
  def dateFormatter(): DateFormatter = {
    return new DateFormatter
  }

  @Bean
  def calendarFormatter(messageSource: MessageSource): CalendarFormatter = {
    val bean = new CalendarFormatter
    bean.messageSource = messageSource

    return bean
  }

  @Bean
  def mvcConversionService(dateFormatter: DateFormatter, calendarFormatter: CalendarFormatter): ConversionService = {
    val bean = new FormattingConversionServiceFactoryBean();
    bean.setFormatters(Set(dateFormatter, calendarFormatter).asJava)
    bean.afterPropertiesSet()
    return bean.getObject()
  }

  private def getConverters(): java.util.Set[Converter[_, _]] = {
    return Set().asJava
  }

  @Bean //@ConditionalOnBean(Array(classOf[DispatcherServlet]))
  def dispatcherRegistration(dispatcherServlet: DispatcherServlet): ServletRegistrationBean = {
    val srb = new ServletRegistrationBean(dispatcherServlet)
    srb.addUrlMappings("/*")

    srb
  }

}

// @Configuration
// @EnableAutoConfiguration
// @Import(Array(classOf[WebAppConfig], classOf[Application]))
// class WebApplication

object Application extends App {
  SpringApplication.run(classOf[Application])
}

