package main.scala.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import javax.persistence.CascadeType
import javax.persistence.PrimaryKeyJoinColumn
import java.util.Calendar
import main.scala.model.personal.Address
import javax.persistence.ManyToOne

@Entity
@Table(name = "COACH")
@PrimaryKeyJoinColumn(name="staffId")
class Coach(stName: String,
    stSurnames: Seq[String],
    stEmail: String,
    stTelephones: Seq[String],
    stAddress: Address, 
    stNif: String,
    stBirth: Calendar,
    licen: License)
    extends Staff(stName, stSurnames, stEmail, stTelephones, stAddress, stNif, stBirth) {
  
  @BeanProperty
  @ManyToOne(optional=false, fetch = FetchType.LAZY, 
      cascade = Array(CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE))
  @JoinColumn(name = "licenseId")
  var coachLicense: License = licen
  
  override
  def toString = "Coach\n" + super.toString + 
  					"\nLicense: " + this.coachLicense  

}