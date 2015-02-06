package com.vac.manager.auth.model.integration

import com.vac.manager.auth.model.UserAuthService
import com.vac.manager.util.FederationBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class UserAuthAdapterForSpringSecurityIntegration extends UserDetailsService {
  @Autowired
  var federationBean: FederationBean = _

  @Autowired
  var authService: UserAuthService = _

  def loadUserByUsername(username: String): UserDetails = {
    val fedId = federationBean.getId

    val user = authService loadUserByUsername (fedId, username)

    user match {
      case None => throw new UsernameNotFoundException("Credentials invalid")
      case Some(user) => return user
    }
  }
}
