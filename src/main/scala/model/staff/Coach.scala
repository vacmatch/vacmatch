package main.scala.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import javax.persistence.CascadeType

@Entity
@Table(name = "COACH")
class Coach extends Staff {
  
  @BeanProperty
  @OneToOne(optional=false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "playerStatsId")
  var coachLicense: License = _

  override
  def toString = "Coach\n" + super.toString + 
  					"\nLicense: " + this.coachLicense  

}