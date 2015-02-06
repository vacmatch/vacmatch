package com.vac.manager.auth.controllers.admin

import com.vac.manager.auth.model.{ User, UserAuthService, UserRole }
import com.vac.manager.controllers.utils.UrlGrabber
import com.vac.manager.util.{ FederationBean, Layout }
import com.vac.manager.util.Sugar.splitEither
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Layout("layouts/default_admin")
@Controller
class UserAdminController extends UrlGrabber {

  implicit class CrudUser(@BeanProperty val props: User) extends UrlGrabber {

    def getEditLink() =
      getUrl("UserAdminController.editForm", "user" -> props.username)

    def getRemoveLink() =
      getUrl("UserAdminController.delete", "user" -> props.username)

  }

  implicit class EditableUser(base: User) extends User {
    username = base.username
    fullName = base.fullName
    email = base.email
    federation = base.federation
    locked = base.locked
    enabled = base.enabled
    lastPasswordChange = base.lastPasswordChange
    roles = base.roles

    @BeanProperty
    lazy val textRoles: java.util.Set[String] =
      roles.asScala.map { role => role.name }.asJava
  }

  @Autowired
  var userAuthService: UserAuthService = _

  @Autowired
  var fed: FederationBean = _

  def registerForm(
    @RequestParam(value = "done", required = false) done: Boolean
  ): ModelAndView = {

    val roles = userAuthService.getRoles(fed.getId)

    val user = new EditableUser(new User)
    user.roles = new java.util.HashSet

    return new ModelAndView("admin/user/register")
      .addObject("roles", roles.asJava)
      .addObject("user", user)
      .addObject("createUrl", getUrl("UserAdminController.registerForm"))
      .addObject("listUrl", getUrl("UserAdminController.list"))
      .addObject("action", "create")
      .addObject("submitUrl", getUrl("UserAdminController.registerPost"))
  }

  def registerPost(
    @RequestParam username: String,
    @RequestParam fullName: String,
    @RequestParam encPasswd: String,
    @RequestParam password: String,
    @RequestParam textRoles: java.util.List[String]
  ) = {

    if (password != encPasswd) {
      throw new RuntimeException("Password not matching")
    }

    val allRoles: Seq[UserRole] = userAuthService.getRoles(fed.getId)

    val r = userAuthService.createUser(
      fed.getId, username, encPasswd, "noemail", fullName,
      textRoles.asScala.toList
    )

    def _registerForm(done: java.lang.Boolean, username: String = ""): String =
      getUrl(
        "UserAdminController.registerForm",
        "done" -> done,
        "username" -> username
      )

    r match {
      case None => registerForm(false) // "redirect:" + registerForm(false)
      case Some(_) =>

        textRoles.asScala.foreach { role =>
          userAuthService.addAuthorityToUser(fed.getId, username, role)
        }

        "redirect:" + _registerForm(true, username)
    }
  }

  def list(): ModelAndView = {
    val users = userAuthService.findAllUsers(fed.getId).map(new CrudUser(_)).asJava

    return new ModelAndView("admin/user/list")
      .addObject("users", users)
      .addObject("createUrl", getUrl("UserAdminController.registerForm"))
      .addObject("listUrl", getUrl("UserAdminController.list"))
  }
  def editForm(
    @RequestParam("user") user: String,
    @RequestParam(value = "done", required = false) done: Boolean
  ): ModelAndView = {

    val maybeUser = userAuthService.loadUserByUsername(fed.getId, user)

    maybeUser match {
      case None => throw new RuntimeException("User " + user + " was not found at fedId " + fed.getId)
      case Some(u) =>
        val user = new EditableUser(u)
        user.props.encPasswd = ""

        println("Getting username = " + user.username)

        val roles = userAuthService.getRoles(fed.getId)

        return new ModelAndView("admin/user/register")
          .addObject("roles", roles.asJava)
          .addObject("user", user)
          .addObject("createUrl", getUrl("UserAdminController.registerForm"))
          .addObject("listUrl", getUrl("UserAdminController.list"))
          .addObject("action", "edit")
          .addObject("submitUrl", getUrl("UserAdminController.editPost"))
    }
  }

  def editPost(
    @RequestParam("username") username: String,
    @RequestParam("fullName") fullName: String,
    @RequestParam("encPasswd") encPasswd: String,
    @RequestParam("password") password: String,
    @RequestParam("textRoles") textRoles: java.util.List[String]
  ): String = {

    val maybeUser = userAuthService.loadUserByUsername(fed.getId, username)

    maybeUser match {
      case None => throw new RuntimeException("User does not exist")
      case Some(user) =>
        val allRoles: Seq[UserRole] = userAuthService.getRoles(fed.getId)

        val savedTextRoles = user.roles.asScala.map(_.name)
        // val newRoles = new java.util.HashSet(allRoles.filter(role => textRoles.contains(role.name)).asJava)

        def addRoles(rolesToAdd: Seq[String]) = {
          rolesToAdd.map { role =>
            if (userAuthService.addAuthorityToUser(fed.getId, username, role))
              Right("Role " + role + " added successfully")
            else
              Left("Role " + role + " could not be added")
          }
        }

        def removeRoles(rolesToRemove: Seq[String]) = {
          rolesToRemove.map { role =>
            if (userAuthService.removeAuthorityFromUser(fed.getId, username, role))
              Right("Role " + role + " removed successfully")
            else
              Left("Role " + role + " could not be removed")
          }
        }

        def mayModifyPassword() = {
          if (encPasswd != "" && password != "") {
            if (password != encPasswd) {
              throw new RuntimeException("Password not matching")
            }
            if (userAuthService.modifyPassword(fed.getId, username, encPasswd))
              Right("User password changed successfully")
          }
          Left("Passwords do not match")
        }

        val addedRoles = addRoles(textRoles.asScala.filter(!savedTextRoles.contains(_)))
        val removedRoles = removeRoles(savedTextRoles.toSeq.filter(!textRoles.contains(_)))
        val modifiedPassword = mayModifyPassword()

        // // If username changes, change it // ?? requires aditional parameter originalUsername
        val modifiedRealName =
          if (userAuthService.modifyRealName(fed.getId, username, fullName))
            Right("Real name changed successfully")
          else
            Left("Real name could not be changed")

        val ops = addedRoles ++ removedRoles ++ List(modifiedPassword, modifiedRealName)

        val (err, info) = splitEither(ops)

        // val modifiedEmail = userAuthService.modifyEmail(fed.getId, username, email)

        def redir(err: Seq[String], info: Seq[String]): String = {
          val params = List("user" -> username) ++ err.map("error" -> _) ++ info.map("info" -> _)

          getUrl("UserAdminController.editForm", params: _*)

        }

        return redir(err, info)
    }
  }

}
