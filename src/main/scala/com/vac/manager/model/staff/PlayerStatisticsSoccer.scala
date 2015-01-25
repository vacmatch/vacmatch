package com.vac.manager.model.staff

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
  @Column(nullable = false)
  var convened: Long = 0

  @BeanProperty
  @Column(nullable = false)
  var opener: Long = 0

  @BeanProperty
  @Column(nullable = false)
  var surrogate: Long = 0
  
  @BeanProperty
  @Column(nullable = false)
  var goals: Long = 0

  @BeanProperty
  @Column(nullable = false)
  var yellowCards: Long = 0

  @BeanProperty
  @Column(nullable = false)
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


