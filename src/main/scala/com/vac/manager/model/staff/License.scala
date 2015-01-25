package com.vac.manager.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.SequenceGenerator
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import scala.beans.BeanProperty
import javax.persistence.Column

@Entity
@Table(name = "LICENSE")
class License(liType: String, liDef: String) {
  
  @Id
  @SequenceGenerator(name="licenseIdGenerator", sequenceName="license_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "licenseIdGenerator")
  var licenseId: Long = _

  @BeanProperty
  @Column(unique = true)
  var licenseType: String = liType
  
  @BeanProperty
  @Column
  var licenseDefinition: String = liDef
  
  def this() = this(null, null)

}



