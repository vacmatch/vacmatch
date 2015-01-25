package com.vac.manager.model.personal

import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column

@Entity
@Table(name = "ADDRESS_SPAIN")
@PrimaryKeyJoinColumn(name="addressId")
class AddressSpain(argRoad: String, argNum: String, argFlat: String,
    argPostCod: Int, argLocality: String, argProvince: String, country: String)
    extends Address(country) {
    
  @BeanProperty
  @Column
  var road: String = argRoad
    
  @BeanProperty
  @Column
  var number: String = argNum
    
  @BeanProperty
  @Column
  var flat: String = argFlat
    
  @BeanProperty
  @Column
  var postCode: Int = argPostCod
    
  @BeanProperty
  @Column
  var locality: String = argLocality
  
  @BeanProperty
  @Column
  var province: String = argProvince

  override
  def toString = this.road + ", " + this.number + " - " + this.flat + "\n" +
		  		 this.postCode + " " + this.locality + ", " + this.province + "\n" +
		  		 super.toString
		  	
		  		 
}


