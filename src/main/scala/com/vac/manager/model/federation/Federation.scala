package com.vac.manager.model.federation

import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import scala.beans.BeanProperty

@Entity
class Federation {

  @Id
  @Column(name = "FEDID")
  @BeanProperty
  var fedId: java.lang.Long = _

  @Column
  @BeanProperty
  var fedName: String = _

}

@Entity
class FederationDomainName {

  @Id
  @Column
  @BeanProperty
  var dns: String = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "FEDID")
  var fed: Federation = _
}
