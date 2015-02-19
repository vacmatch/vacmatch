package com.vac.manager.auth.model

import com.vac.manager.model.federation.Federation
import javax.persistence.FetchType
import javax.persistence.{ Column, Entity, Id }
import javax.persistence.ManyToOne
import scala.beans.BeanProperty

@Entity
class UserRole {
  @Id
  @BeanProperty
  var name: String = _

  @Column
  @BeanProperty
  var humanName: String = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  var federation: Federation = _
}
