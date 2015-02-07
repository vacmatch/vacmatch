package com.vac.manager.model.staff

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
import com.vac.manager.model.personal.Address
import javax.persistence.ManyToOne
import com.vac.manager.model.federation.Federation

@Entity
@Table(name = "COACH")
@PrimaryKeyJoinColumn(name="staffId")
class Coach(stName: String,
    stSurnames: String,
    stEmail: String,
    stTelephones: String,
    stAddress: Address, 
    stNif: String,
    stBirth: Calendar,
    stFederation: Federation)
    extends StaffMember(stName, stSurnames, stEmail, stTelephones, stAddress, stNif,
        stBirth, stFederation) {
  
  override
  def toString = "Coach\n" + super.toString

}