package com.vac.manager

import java.util.ArrayList
import javax.servlet.ServletRequest
import javax.sql.DataSource

import com.vac.manager.auth.model.FederationUserDetailsService
import com.vac.manager.controllers.conversions.{ CalendarFormatter, DateFormatter }
import com.vac.manager.util.{ FederationBean, FederationBeanImpl, TenantFilter, ThymeleafLayoutInterceptor }
import org.apache.log4j.LogManager
import org.resthub.web.springmvc.router.RouterConfigurationSupport
import org.springframework.beans.factory.annotation.{ Autowired, Value }
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.{ FilterRegistrationBean, ServletRegistrationBean }
import org.springframework.context.MessageSource
import org.springframework.context.annotation.{ Bean, ComponentScan, Configuration, Import, Lazy, Scope, ScopedProxyMode }
import org.springframework.core.annotation.Order
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.Environment
import org.springframework.format.support.FormattingConversionServiceFactoryBean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{ EnableWebSecurity, WebSecurityConfigurerAdapter }
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.{ InterceptorRegistry, ResourceHandlerRegistry }
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import org.thymeleaf.templateresolver.TemplateResolver

import scala.collection.JavaConverters._

@Lazy
@Configuration // You should not use the @EnableWebMvc annotation
class WebAppConfig() extends RouterConfigurationSupport {

  override protected def isHandlerMappingReloadEnabled() = { true }

  @Override
  def listRouteFiles(): ArrayList[String] = {
    val routeFiles = new ArrayList[String]
    routeFiles.add("classpath*:/routes.conf")
    routeFiles
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
    new ThymeleafLayoutInterceptor
  }

  override def addInterceptors(registry: InterceptorRegistry) = {
    registry.addInterceptor(layoutInterceptor)
  }
}

@Configuration
@EnableWebMvcSecurity
@Order(5)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  override def configure(http: HttpSecurity) = {

    val filter: CharacterEncodingFilter = new CharacterEncodingFilter()
    filter.setEncoding("UTF-8")
    filter.setForceEncoding(true)
    http.addFilterBefore(filter, classOf[CsrfFilter])

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
      .antMatchers("/manage/**")
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
  var env: Environment = _

  @Autowired
  def configureGlobal(auth: AuthenticationManagerBuilder) = {

    val logger = LogManager.getLogger(classOf[WebSecurityConfig])
    val authConfig = auth.userDetailsService(userDetailsService())

    val setupConfig = env.getProperty("SETUP", "")

    if (setupConfig.nonEmpty) {
      val Array(user, pw) = setupConfig.split(":")
      logger.info(s"We are using user=${user} and password=$pw as SETUP")

      authConfig
        .and()
        .inMemoryAuthentication()
        .withUser(user).password(pw).roles("ROOT", "ADMINFED")
    } else {
      logger.warn("We are not using any predefined SETUP variable for " +
        "setting up the users")
      authConfig
    }

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
    new FederationUserDetailsService()
  }

}

@Lazy
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = Array("com.vac.manager"))
@Import(Array(classOf[WebAppConfig], classOf[I18nableApplication], classOf[WebSecurityConfig]))
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
@Order(10)
class Application extends org.springframework.boot.context.web.SpringBootServletInitializer {

  override protected def configure(application: org.springframework.boot.builder.SpringApplicationBuilder): org.springframework.boot.builder.SpringApplicationBuilder = {
    application.sources(classOf[Application])
    application.web(true)
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  def federationBean(req: ServletRequest): FederationBean = {
    new FederationBeanImpl(req)
  }

  @Autowired
  var env: Environment = _

  @Bean
  def dataSource(): DataSource = {

    val dataSource = new DriverManagerDataSource()

    Option(env.getProperty("OVERRIDE_DATABASE_URL"))
      .filter(_.nonEmpty)
      .orElse(Option(env.getProperty("DATABASE_URL")))
      .filter(_.nonEmpty)
      .map(_.replace("postgres", "postgresql"))
      .map(new java.net.URI(_))
      .map {
        url =>
          val Array(user, pw) = url.getUserInfo.split(":")

          val uri = "jdbc:" + url.getScheme + "://" + url.getHost + ":" +
            url.getPort + url.getPath + s"?user=${user}&password=${pw}"

          dataSource.setUrl(uri)

      }.getOrElse {
        dataSource.setUrl(env.getProperty("spring.datasource.url"))
        dataSource.setUsername(env.getProperty("spring.datasource.username"))
        dataSource.setPassword(env.getProperty("spring.datasource.password"))
      }

    // dataSource.setDriverClassName(env.getProperty("DATABASE_DRIVER"))
    // dataSource.setUrl(url)
    // dataSource.setUsername(user)
    // dataSource.setPassword(pw)

    dataSource

    /*
    val connectionFactory: ConnectionFactory =
      new DriverManagerConnectionFactory(url, null)

    val poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null)
    val connectionPool = new GenericObjectPool(poolableConnectionFactory)

    poolableConnectionFactory.setPool(connectionPool)

    new PoolingDataSource(connectionPool)
    */
  }

  @Bean
  def dateFormatter(): DateFormatter = {
    new DateFormatter
  }

  @Bean
  def calendarFormatter(messageSource: MessageSource): CalendarFormatter = {
    val bean = new CalendarFormatter
    bean.messageSource = messageSource

    bean
  }

  @Bean
  def mvcConversionService(dateFormatter: DateFormatter, calendarFormatter: CalendarFormatter): ConversionService = {
    val bean = new FormattingConversionServiceFactoryBean
    bean.setFormatters(Set(dateFormatter, calendarFormatter).asJava)
    bean.afterPropertiesSet
    bean getObject
  }

  private def getConverters(): java.util.Set[Converter[_, _]] = {
    Set().asJava
  }

}

// @Configuration
// @EnableAutoConfiguration
// @Import(Array(classOf[WebAppConfig], classOf[Application]))
// class WebApplication

object Application extends App {

  override def main(args: Array[String]) = {
    val app = SpringApplication.run(classOf[WebApplication])
    app getBean classOf[TemplateResolver] setCacheable false
  }

}

