package com.vac.manager.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

trait UserAuthService {
  def loadUserByUsername(fedId: Long, username: String): Option[User]
  def findAllUsers(fedId: Long): Seq[User]
  def getFederationAuthorities(fedId: Long): Seq[_ >: GrantedAuthority]

  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String): Option[UserDetails]
  def createUser(fedId: Long, username: String, password: String, email: String, fullName: String, textRoles: List[String]): Option[UserDetails]
  def addAuthorityToUser(fedId: Long, username: String, role_id: String): Boolean
  def userHasAuthority(fedId: Long, username: String, role_id: String): Option[UserRole]
  def removeAuthorityFromUser(fedId: Long, username: String, role_id: String): Boolean

  def modifyUserName(fedId: Long, username: String, new_username: String): Boolean
  def modifyRealName(fedId: Long, username: String, new_realname: String): Boolean
  def modifyEmail(fedId: Long, username: String, new_email: String): Boolean
  def modifyPassword(fedId: Long, username: String, new_password: String): Boolean

  def createRole(fedId: Long, role_id: String, role_name: String): Option[UserRole]
  def getRoles(fedId: Long): Seq[UserRole]

}

