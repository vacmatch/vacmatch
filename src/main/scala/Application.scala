package main.scala

import java.util.ArrayList
import org.resthub.web.springmvc.router.RouterConfigurationSupport
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.{ Bean, ComponentScan, Configuration, Import }
import scala.collection.JavaConverters._

@Configuration
@ComponentScan(basePackages = Array("main.scala")) // You should not use the @EnableWebMvc annotation
class WebAppConfig() extends RouterConfigurationSupport {

  @Override
  def listRouteFiles(): ArrayList[String] = {
    val routeFiles = new ArrayList[String];
    routeFiles.add("classpath:/routes.conf");
    return routeFiles;
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

