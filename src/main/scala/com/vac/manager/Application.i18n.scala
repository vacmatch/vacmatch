package com.vac.manager

import com.vacmatch.util.i18n.I18n
import com.vacmatch.util.i18n.I18nScaposerBean
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils

class LocaleConfigurerFilter extends org.springframework.web.filter.OncePerRequestFilter {
  private[this] var localeResolver: Option[org.springframework.web.servlet.LocaleResolver] = None

  @throws[javax.servlet.ServletException]
  override protected def initFilterBean() = {
    val wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext())

    val resolvers = wac.getBeansOfType(classOf[org.springframework.web.servlet.LocaleResolver])

    if (resolvers.size() == 1)
      localeResolver = Some(resolvers.values.iterator.next)

  }

  @throws[javax.servlet.ServletException]
  @throws[java.io.IOException]
  override protected def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) = {
    localeResolver
      .map(_.resolveLocale(request))
      .map(locale => LocaleContextHolder.setLocale(locale))

    chain.doFilter(request, response)

    localeResolver.map(_ => LocaleContextHolder.resetLocaleContext)
  }

}

@Configuration
class I18nableApplication {
  @Bean
  def localeConfigurerFilter(): LocaleConfigurerFilter = {
    new LocaleConfigurerFilter
  }

  @Bean
  def i18nTraitBean(): I18n = {
    return new I18nScaposerBean
  }

  @Bean
  def localeResolver(): org.springframework.web.servlet.i18n.SessionLocaleResolver = {
    val resolver = new org.springframework.web.servlet.i18n.SessionLocaleResolver()
    resolver.setDefaultLocale(new java.util.Locale("es"))

    resolver
  }

  @Bean
  def localeChangeInterceptor(): org.springframework.web.servlet.i18n.LocaleChangeInterceptor = {
    val interceptor = new org.springframework.web.servlet.i18n.LocaleChangeInterceptor
    interceptor.setParamName("hl")

    // org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)
    interceptor
  }

  @Bean
  def thymeleafDialectForI18n(): com.vacmatch.util.i18n.thymeleaf.I18nThymeleafDialect = {
    val dialect = new com.vacmatch.util.i18n.thymeleaf.I18nThymeleafDialect(i18nTraitBean())
    dialect
  }

  @Bean
  def thymeleafDialectForExplicitI18n(): com.vacmatch.util.i18n.thymeleaf.I18nThymeleafExplicitDialect = {
    new com.vacmatch.util.i18n.thymeleaf.I18nThymeleafExplicitDialect(i18nTraitBean())
  }
}
