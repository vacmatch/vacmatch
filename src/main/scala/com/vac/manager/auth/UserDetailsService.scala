package com.vac.manager.auth.model

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.{ UserDetailsService => Base }
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import scala.beans.BeanProperty

@Transactional
@Repository
class FederationUserDetailsService extends GenericDaoJPA[User, java.lang.Long](classOf[User]) with Base {

  def loadUserByUsername(username: String): UserDetails = {
    println("LOAD USER BY USERNAME IS BEING CALLED WITH " + username)

    val user = entityManager
      .createQuery(
        "SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username",
        classOf[User]
      )
      .setParameter("username", username)
      .getResultList

    if (user.size > 0)
      return user get 0

    throw new UsernameNotFoundException("Credentials invalid")
  }

}
