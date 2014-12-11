package main.scala

import java.util.ArrayList
import org.resthub.web.springmvc.router.RouterConfigurationSupport
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.{ FilterRegistrationBean, ServletRegistrationBean }
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.{ Bean, ComponentScan, Configuration }
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.view.InternalResourceViewResolver
import scala.collection.JavaConverters._

@Configuration
@ComponentScan(basePackages = Array("main.scala")) // You should not use the @EnableWebMvc annotation
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
}

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(Array(classOf[WebAppConfig]))
class Application {
}

object Application extends App {
  SpringApplication.run(classOf[Application]);
}

