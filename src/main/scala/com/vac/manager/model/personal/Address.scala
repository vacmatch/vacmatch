package com.vac.manager.model.personal

import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table(name = "ADDRESS")
@PrimaryKeyJoinColumn(name="addressId")
class Address(addLine: String, argPostCod: String, 
    argLocality: String, argProvince: String, nation: String) {
 
  @Id
  @SequenceGenerator(name="addressIdGenerator", sequenceName="address_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "addressIdGenerator")
  var addressId: Long = _
  
  @BeanProperty
  @Column
  var addressLine: String = addLine
    
  @BeanProperty
  @Column
  var postCode: String = argPostCod
    
  @BeanProperty
  @Column
  var locality: String = argLocality
  
  @BeanProperty
  @Column
  var province: String = argProvince
  
  @BeanProperty
  @Column
  var country: String = nation
  
  override
  def equals(obj: Any): Boolean = {
    if((obj == null) || (!obj.isInstanceOf[Address]))
      return false
    var addObj: Address = obj.asInstanceOf[Address]
    (addObj.addressId == this.addressId) &&
    (addObj.addressLine == this.addressLine) &&
    (addObj.postCode == this.postCode) &&
    (addObj.locality == this.locality) &&
    (addObj.province == this.province) &&
    (addObj.country == this.country)
  }
  
  override
  def toString = "(" + this.addressId +") " + this.addressLine + "\n" +
		  		 this.postCode + " " + this.locality + ", " + this.province + "\n" +
		  		 this.country

  def this() = this("","","","","")
  
}


