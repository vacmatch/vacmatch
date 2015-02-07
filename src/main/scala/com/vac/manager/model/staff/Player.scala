package com.vac.manager.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.FetchType
import javax.persistence.CascadeType
import javax.persistence.PrimaryKeyJoinColumn
import java.util.Calendar
import com.vac.manager.model.personal.Address
import com.vac.manager.model.federation.Federation

@Entity
@Table(name = "PLAYER")
@PrimaryKeyJoinColumn(name="staffId")
class Player(stName: String,
    stSurnames: String,
    stEmail: String,
    stTelephones: String,
    stAddress: Address,
    stNif: String,
    stBirth: Calendar,
    stFederation: Federation,
    num: Int)
    extends Staff(stName, stSurnames, stEmail, stTelephones, stAddress, stNif,
        stBirth, stFederation)  {
  
  @BeanProperty
  @Column
  var playerNumber: Int = num
  
  override
  def toString = "Player\n" + super.toString + 
  					"\nNumber: " + this.playerNumber
}


