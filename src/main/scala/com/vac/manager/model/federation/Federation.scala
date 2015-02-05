package com.vac.manager.model.federation

import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import scala.beans.BeanProperty

@Entity
class Federation {

  @Id
  @Column(name = "FEDID")
  @SequenceGenerator(name = "fedIdGenerator", sequenceName = "fed_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "fedIdGenerator")
  @BeanProperty
  var fedId: java.lang.Long = _

  @Column(nullable = false, unique = true)
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
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "FEDID", nullable = false)
  var fed: Federation = _
}
