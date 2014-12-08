package main.scala.model.personal

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Inheritance
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.InheritanceType
import scala.beans.BeanProperty
import javax.persistence.Column

@Entity
@Table(name = "ADDRESS")
@Inheritance(strategy=InheritanceType.JOINED)
class Address {
  
  @Id
  @SequenceGenerator(name="addressIdGenerator", sequenceName="address_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressIdGenerator")
  var addressId: Long = _
  
  @BeanProperty
  @Column
  var country: String =_
  
  override
  def toString(): String = this.country + "\n"
    
}