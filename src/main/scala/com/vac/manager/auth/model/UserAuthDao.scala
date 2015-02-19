package com.vac.manager.auth.model

import com.vac.manager.model.generic.GenericDao

trait UserAuthDao extends GenericDao[User, java.lang.Long] {

  def loadUserByUsername(fedId: java.lang.Long, username: String): Option[User]
  def findAllByFed(fedId: java.lang.Long): java.util.List[User]
  def findAllRoles(fedId: java.lang.Long): java.util.List[UserRole]
  def saveRole(role: UserRole)

}
