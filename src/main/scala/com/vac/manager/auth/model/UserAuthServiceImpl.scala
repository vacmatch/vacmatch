package com.vac.manager.auth.model

import com.vac.manager.model.federation.daojpa._
import com.vac.manager.model.federation.FederationDao
import com.vac.manager.service.federation.FederationService
import com.vac.manager.util.FederationBean
import java.util.Date
import java.util.GregorianCalendar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConverters._

@Service("userAuthService")
@Transactional
class UserAuthServiceImpl extends UserAuthService {

  @Autowired
  var userAuthDao: UserAuthDao = _

  def loadUserByUsername(fedId: Long, user: String): Option[User] = {
    userAuthDao.loadUserByUsername(fedId, user)
  }

  def findAllUsers(fedId: Long): Seq[User] = {
    userAuthDao.findAllByFed(fedId).asScala
  }

  def getFederationAuthorities(fedId: Long): Seq[_ >: GrantedAuthority] = {
    userAuthDao.findAllRoles(fedId).asScala
  }

  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String): Option[UserDetails] = {
    createUser(fedId, username, password, email, fullName, List())
  }

  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String, textRoles: List[String]): Option[UserDetails] = {

    val allRoles = userAuthDao.findAllRoles(fedId).asScala.map(role => (role.name, role)).toMap

    val roles = textRoles.map { s => allRoles(s) }

    _createUser(fedId, username, password, email, fullName, roles)
  }

  def _createUser(fedId: Long, username: String, password: String, email: String, fullName: String, roles: List[UserRole]): Option[UserDetails] = {
    val u = new User
    val fed = userAuthDao.entityManager.find(classOf[Federations], fedId.asInstanceOf[java.lang.Long])

    try {
      val existing = loadUserByUsername(fedId, username)

      // if existing is not null and exists
      Option(existing).flatten
        .map(e => throw new RuntimeException("User already exists" + e));

      u.federation = fed
      u.username = username
      u.encPasswd = password
      u.email = email
      u.fullName = fullName
      u.enabled = true
      u.lastPasswordChange = new GregorianCalendar()
      u.lastPasswordChange.setTime(new Date())
      u.locked = false
      u.roles = new java.util.HashSet(roles.asJava)

      userAuthDao.save(u)

      Some(u)

    } catch {
      case e: RuntimeException =>
        println("Something weird happened due to " + e)
        None
    }

  }

  def addAuthorityToUser(fedId: Long, username: String, role_id: String): Boolean = {
    val roles = userAuthDao.findAllRoles(fedId)
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)
    val role_to_add = roles.asScala.find { role => role.name equals role_id }

    maybeUser.exists {
      u =>

        // Test if user already has that role
        val existing_role = u.roles.asScala.find { role => role.name equals role_id }

        (existing_role, role_to_add) match {
          case (None, Some(role)) => {
            u.roles.add(role)
            userAuthDao.save(u)
            true
          }
          case _ => false
        }
    }
  }

  def userHasAuthority(fedId: Long, username: String, role_id: String): Option[UserRole] = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.flatMap(_.roles.asScala.find { role => role.name equals role_id })
  }

  def removeAuthorityFromUser(fedId: Long, username: String, role_id: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.exists { u =>
      val role = u.roles.asScala.find { role => role.name equals role_id }

      role.exists { role =>
        u.roles.remove(role)
        userAuthDao.save(u)
        true
      }
    }
  }

  def modifyUserName(fedId: Long, username: String, new_username: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.filterNot(_.username == new_username).exists {
      u =>
        u.username = new_username
        userAuthDao.save(u)
        true
    }
  }

  def modifyRealName(fedId: Long, username: String, new_realname: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.filterNot(_.fullName == new_realname).exists {
      u =>
        u.fullName = new_realname
        userAuthDao.save(u)
        true
    }
  }

  def modifyEmail(fedId: Long, username: String, new_email: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.filterNot(_.email == new_email)
      .exists {
        u =>
          u.email = new_email
          userAuthDao.save(u)
          true
      }

  }

  def modifyPassword(fedId: Long, username: String, new_password: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser.exists { u =>
      u.encPasswd = new_password
      userAuthDao.save(u)
      true
    }
  }

  def createRole(fedId: Long, role_id: String, role_name: String): Option[UserRole] = {
    if (getRoles(fedId).find(role => role.name equals role_id).nonEmpty)
      return None

    val fed = userAuthDao.entityManager.find(classOf[Federations], fedId.asInstanceOf[java.lang.Long]) // Not getting the fed is a RuntimeException
    val newRole = new UserRole
    newRole.federation = fed
    newRole.name = role_id
    newRole.humanName = role_name

    userAuthDao.saveRole(newRole)
    Some(newRole)
  }

  def getRoles(fedId: Long): Seq[UserRole] = {
    userAuthDao.findAllRoles(fedId).asScala
  }
}
