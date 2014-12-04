package main.scala.model.staff

import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.PrimaryKeyJoinColumn

@Entity
@Table(name = "PLAYER_STATISTICS_SOCCER")
@PrimaryKeyJoinColumn(name="playerStatsId")
class PlayerStatisticsSoccer extends PlayerStatistics {
  
  @BeanProperty
  @Column
  var convened: Long = 0

  @BeanProperty
  @Column
  var opener: Long = 0

  @BeanProperty
  @Column
  var surrogate: Long = 0
  
  @BeanProperty
  @Column
  var goals: Long = 0

  @BeanProperty
  @Column
  var yellowCards: Long = 0

  @BeanProperty
  @Column
  var redCards	: Long = 0
  
  override
  def toString = "Player statistics" +
    				"\nConvended games: " + this.convened +
  					"\nOpener games: " + this.opener + 
  					"\nSurrogate games: " + this.surrogate +
  					"\nGoals: " + this.goals +
  					"\nYellow cards: " + this.yellowCards +
  					"\nRed cards: " + this.redCards 
}


