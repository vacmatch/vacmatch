package com.vac.manager

import controllers.conversions.{ CalendarFormatter, DateFormatter }
import java.util.ArrayList
import javax.servlet.ServletRequest
import org.resthub.web.springmvc.router.RouterConfigurationSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.{ FilterRegistrationBean, ServletRegistrationBean }
import org.springframework.context.MessageSource
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.context.annotation.{ Bean, ComponentScan, Configuration, Import, Lazy, Scope }
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.format.Formatter
import org.springframework.format.support.FormattingConversionServiceFactoryBean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.{ InterceptorRegistry, ResourceHandlerRegistry }
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import org.thymeleaf.templateresolver.TemplateResolver
import scala.collection.JavaConverters._
import util.{ FederationBean, FederationBeanImpl, TenantFilter, ThymeleafLayoutInterceptor }
import auth.model.FederationUserDetailsService

@Lazy
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
@EnableWebMvcSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  override def configure(http: HttpSecurity) = {
    // In order to better understand the implications between these
    // objects, the indentation means things :)

    // format: OFF
    http.authorizeRequests
      .antMatchers("/", "/css/**", "/js/**", "/images/**", "**/favicon.ico")
        .permitAll
      .antMatchers("/admin/**")
        .hasRole("ADMINFED")
      .antMatchers("/_adm/**")
        .hasRole("ROOT")
      .anyRequest
        .permitAll
      .and
        .csrf
      .and
        .formLogin
          .loginPage("/admin/login")
          .loginProcessingUrl("/admin/login/do")
          .defaultSuccessUrl("/admin")
          .permitAll
      .and
        .rememberMe
          .useSecureCookie(true)
          .tokenValiditySeconds(3600)
      .and
      .logout
        .logoutUrl("/admin/logout")
          .permitAll

    // format: ON

  }

  @Autowired
  def configureGlobalAuth(auth: AuthenticationManagerBuilder) = {

    auth.userDetailsService(userDetailsService())
    /*
    auth.inMemoryAuthentication()
      .withUser("admin").password("admin").roles("ROOT", "ADMINFED")
      .and()
      .withUser("adminfed").password("admin").roles("ADMINFED")
      .and()
      .withUser("coach").password("coach").roles("COACH")
     */
  }

  @Bean
  override protected def userDetailsService(): UserDetailsService = {
    return new FederationUserDetailsService()
  }

}

@Lazy
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan @Import(Array(classOf[WebAppConfig], classOf[WebSecurityConfig]))
class WebApplication extends Application {
  @Bean
  def multiTenantHandler(): FilterRegistrationBean = {
    val frb = new FilterRegistrationBean

    frb.addUrlPatterns("/a/*")
    frb.setFilter(new TenantFilter)

    frb
  }

  @Bean
  def dispatcherRegistration(dispatcherServlet: DispatcherServlet): ServletRegistrationBean = {
    val srb = new ServletRegistrationBean(dispatcherServlet)
    srb.addUrlMappings("/*")

    srb
  }
}

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan
class Application {

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  def federationBean(req: ServletRequest): FederationBean = {
    return new FederationBeanImpl(req);
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

}

// @Configuration
// @EnableAutoConfiguration
// @Import(Array(classOf[WebAppConfig], classOf[Application]))))
// class WebApplication

object Application extends App {
  val app = SpringApplication run classOf[WebApplication]
  app getBean classOf[TemplateResolver] setCacheable false
}

