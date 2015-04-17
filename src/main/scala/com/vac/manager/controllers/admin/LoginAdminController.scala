package com.vac.manager.controllers.admin

import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.util.Layout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@Layout("layouts/default_admin")
class LoginAdminController extends UrlGrabber {

  def loginForm(param: java.util.Map[String, String]): ModelAndView = {

    return new ModelAndView("admin/login")
      .addObject("loginUrl", getUrl("LoginAdminController.login"))
  }

  def login(
    @RequestParam user: String,
    @RequestParam pass: String,
    @Autowired auth: AuthenticationManager) = {
    var token = new UsernamePasswordAuthenticationToken(user, pass)

    auth.authenticate(token)
  }
}
