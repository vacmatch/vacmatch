package main.scala.model.personal

import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column

@Entity
@Table(name = "ADDRESS_SPAIN")
@PrimaryKeyJoinColumn(name="addressId")
class AddressSpain(coun: String) extends Address(coun) {
    
  @BeanProperty
  @Column
  var road: String = _
    
  @BeanProperty
  @Column
  var number: String = _
    
  @BeanProperty
  @Column
  var flat: String = _
    
  @BeanProperty
  @Column
  var postCode: Int = _
    
  @BeanProperty
  @Column
  var locality: String = _
  
  @BeanProperty
  @Column
  var province: String = _

  override
  def toString = this.road + ", " + this.number + " - " + this.flat + "\n" +
		  		 this.postCode + " " + this.locality + ", " + this.province + "\n" +
		  		 super.toString
		  	
		  		 
}


