package com.vac.manager.auth.model

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

  @Autowired
  var federationService: FederationService = _

  def loadUserByUsername(fedId: Long, user: String): Option[User] = {
    return userAuthDao.loadUserByUsername(fedId, user)
  }

  def findAllUsers(fedId: Long): Seq[User] = {
    return userAuthDao.findAllByFed(fedId).asScala
  }

  def getFederationAuthorities(fedId: Long): Seq[_ >: GrantedAuthority] = {
    return userAuthDao.findAllRoles(fedId).asScala
  }

  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String): Option[UserDetails] = {
    return createUser(fedId, username, password, email, fullName, List())
  }

  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String, textRoles: List[String]): Option[UserDetails] = {

    val allRoles = userAuthDao.findAllRoles(fedId).asScala.map(role => (role.name, role)).toMap

    val roles = textRoles.map { s => allRoles(s) }

    return _createUser(fedId, username, password, email, fullName, roles)
  }

  def _createUser(fedId: Long, username: String, password: String, email: String, fullName: String, roles: List[UserRole]): Option[UserDetails] = {
    val u = new User
    val mfed = federationService.find(fedId)
    val fed = mfed.get

    try {
      val existing = loadUserByUsername(fedId, username)
      if (existing != null && !existing.isEmpty) throw new Exception("User already exists" + existing.get);
    } catch {
      case e: Exception =>
        println("Something weird happened due to " + e)
        return None
    }

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

    return Some(u)
  }

  def addAuthorityToUser(fedId: Long, username: String, role_id: String): Boolean = {
    val roles = userAuthDao.findAllRoles(fedId)
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    if (maybeUser.isEmpty)
      return false

    val u = maybeUser.get

    val role_to_add = roles.asScala.find { role => role.name equals role_id }

    // Test if user already has that role
    val existing_role = u.roles.asScala.find { role => role.name equals role_id }

    (existing_role, role_to_add) match {
      case (None, Some(role)) => {
        u.roles.add(role_to_add.get)
        userAuthDao.save(u)
        return true
      }
      case _ => return false
    }
  }

  def userHasAuthority(fedId: Long, username: String, role_id: String): Option[UserRole] = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    maybeUser match {
      case Some(u) =>
        u.roles.asScala.find { role => role.name equals role_id } match {
          case Some(role) =>
            return Some(role)
          case None => return None
        }
      case None => return None
    }
  }

  def removeAuthorityFromUser(fedId: Long, username: String, role_id: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    return maybeUser match {
      case Some(u) =>
        val maybeRole = u.roles.asScala.find { role => role.name equals role_id }
        maybeRole match {
          case Some(role) =>
            u.roles.remove(role)
            userAuthDao.save(u)
            return true
          case _ => false
        }
      case _ => false
    }
  }

  def modifyUserName(fedId: Long, username: String, new_username: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    return maybeUser match {
      case Some(u) =>
        if (u.username == new_username)
          return false

        u.username = new_username
        userAuthDao.save(u)
        return true
      case _ => false
    }
  }

  def modifyRealName(fedId: Long, username: String, new_realname: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    return maybeUser match {
      case Some(u) =>
        if (u.fullName == new_realname)
          return false

        u.fullName = new_realname
        userAuthDao.save(u)
        return true
      case None => false
    }
  }

  def modifyEmail(fedId: Long, username: String, new_email: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    return maybeUser match {
      case Some(u) =>
        if (u.email == new_email)
          return false

        u.email = new_email
        userAuthDao.save(u)
        return true
      case None => false
    }

  }

  def modifyPassword(fedId: Long, username: String, new_password: String): Boolean = {
    val maybeUser = userAuthDao.loadUserByUsername(fedId, username)

    return maybeUser match {
      case Some(u) =>
        u.encPasswd = new_password
        userAuthDao.save(u)
        return true
      case None => false
    }
  }

  def createRole(fedId: Long, role_id: String, role_name: String): Option[UserRole] = {
    if (getRoles(fedId).find(role => role.name equals role_id).nonEmpty)
      return None

    val fed = federationService.find(fedId).get // Not getting the fed is a RuntimeException
    val newRole = new UserRole
    newRole.federation = fed
    newRole.name = role_id
    newRole.humanName = role_name

    userAuthDao.saveRole(newRole)
    return Some(newRole)
  }

  def getRoles(fedId: Long): Seq[UserRole] = {
    return userAuthDao.findAllRoles(fedId).asScala
  }
}
