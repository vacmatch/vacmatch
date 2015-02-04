package com.vac.manager.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence._

@Entity
@Table(name = "PLAYER_STATISTICS")
@Inheritance(strategy=InheritanceType.JOINED)
class PlayerStatistics {
  
  @Id
  @SequenceGenerator(name="playerStatsIdGenerator", sequenceName="playerStats_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "playerStatsIdGenerator")
  var playerStatsId: Long = _
  
}