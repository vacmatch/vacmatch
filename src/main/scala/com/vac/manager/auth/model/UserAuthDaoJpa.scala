package com.vac.manager.auth.model

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import scala.beans.BeanProperty

@Transactional
@Repository("userAuthDao")
class UserAuthDaoJpa
  extends GenericDaoJPA[User, java.lang.Long](classOf[User])
  with UserAuthDao {

  def loadUserByUsername(fedId: java.lang.Long, username: String): Option[User] = {

    val user = entityManager
      .createQuery(
        "SELECT u FROM User u JOIN FETCH u.roles " +
          "WHERE u.federation.fedId = :fedId " +
          "AND u.username = :username",
        classOf[User]
      )
      .setParameter("fedId", fedId)
      .setParameter("username", username)
      .getResultList

    if (user.size > 0)
      return Some(user get 0)
    else
      return None
  }

  def findAllByFed(fedId: java.lang.Long): java.util.List[User] = {
    return entityManager.createQuery(
      "SELECT u FROM User u " +
        "WHERE u.federation.fedId = :fedId", classOf[User]
    )
      .setParameter("fedId", fedId)
      .getResultList
  }

  def findAllRoles(fedId: java.lang.Long): java.util.List[UserRole] = {
    return entityManager
      .createQuery(
        "SELECT r FROM UserRole r " +
          "WHERE r.federation.fedId = :fedId",
        classOf[UserRole]
      )
      .setParameter("fedId", fedId)
      .getResultList
  }

  def saveRole(role: UserRole) = {
    entityManager.persist(role)
  }

}
