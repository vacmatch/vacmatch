package main.scala.model.team

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import scala.beans.BeanProperty
import javax.persistence.Column

@Entity
@Table(name="EQUIPMENT")
class Equipment(eqType: String, eqShirt: String, eqShorts: String, eqTights: String) {

  @Id
  @SequenceGenerator(name="equipmentIdGenerator", sequenceName="equipment_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipmentIdGenerator")
  var equipmentId: Long = _
  
  @BeanProperty
  @Column(unique = true)
  var equipmentType: String = eqType
  
  @BeanProperty
  @Column
  var equipmentShirtColor: String = eqShirt
  
  @BeanProperty
  @Column
  var equipmentShortsColor: String = eqShorts

  @BeanProperty
  @Column
  var equipmentTightsColor: String = eqTights
  
  def this() = this(null, null, null, null)
  
}



