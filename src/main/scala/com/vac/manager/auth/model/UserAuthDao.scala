package com.vac.manager.auth.model

import com.vac.manager.model.generic.GenericDao
import javax.persistence.EntityManager

trait UserAuthDao extends GenericDao[User, java.lang.Long] {

  var entityManager: EntityManager // hack: make this public for the while

  def loadUserByUsername(fedId: java.lang.Long, username: String): Option[User]
  def findAllByFed(fedId: java.lang.Long): java.util.List[User]
  def findAllRoles(fedId: java.lang.Long): java.util.List[UserRole]
  def saveRole(role: UserRole)

}
