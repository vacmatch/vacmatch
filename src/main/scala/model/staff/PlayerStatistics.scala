package main.scala.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table(name = "PLAYER_STATISTICS")
class PlayerStatistics {
  
  @Id
  @SequenceGenerator(name="playerStatsIdGenerator", sequenceName="playerStats_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playerStatsIdGenerator")
  var playerStatsId: Long = _
  
}