package main.scala.model.staff

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
import main.scala.model.personal.Address

@Entity
@Table(name = "PLAYER")
@PrimaryKeyJoinColumn(name="staffId")
class Player(stName: String,
    stSurnames: Seq[String],
    stEmail: String,
    stTelephones: Seq[String],
    stAddress: Address,
    stNif: String,
    stBirth: Calendar,
    num: Int)
    extends Staff(stName, stSurnames, stEmail, stTelephones, stAddress, stNif, stBirth)  {
  
  @BeanProperty
  @Column
  var playerNumber: Int = num
  
  @BeanProperty
  @OneToOne(optional=false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "playerStatsId", nullable = false)
  var playerStatistics: PlayerStatistics = new PlayerStatistics()

  override
  def toString = "Player\n" + super.toString + 
  					"\nNumber: " + this.playerNumber +
  					"\n" + this.playerStatistics.toString()
}


