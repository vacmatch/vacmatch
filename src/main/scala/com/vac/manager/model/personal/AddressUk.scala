package com.vac.manager.model.personal

import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@Table(name = "ADDRESS_UK")
@PrimaryKeyJoinColumn(name="addressId")
class AddressUk(country: String) extends Address(country) {
  
  @BeanProperty
  @Column
  var number: String = _
  
  @BeanProperty
  @Column  
  var road: String = _
  
  @BeanProperty
  @Column  
  var locality: String = _
  
  @BeanProperty
  @Column  
  var postTown: String = _
  
  @BeanProperty
  @Column  
  var postCode: String = _
  
  override
  def toString = this.number + " " + this.road + "\n" +
		  		 this.locality + ", " + this.postTown + "\n" +
		  		 this.postCode + "\n" +
		  		 super.toString 


}


