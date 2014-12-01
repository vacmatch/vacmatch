package main.scala.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.FetchType
import javax.persistence.CascadeType

@Entity
@Table(name = "PLAYER")
class Player extends Staff  {
  
  @BeanProperty
  @Column
  var playerNumber: Int = _
  
  @BeanProperty
  @OneToOne(optional=false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "playerStatsId")
  var playerStatistics: PlayerStatistics = new PlayerStatistics()

  override
  def toString = "Player\n" + super.toString + 
  					"\nNumber: " + this.playerNumber +
  					"\n" + this.playerStatistics.toString()
}


