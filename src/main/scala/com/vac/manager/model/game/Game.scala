package com.vac.manager.model.game

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import scala.beans.BeanProperty
import javax.persistence.Column
import com.vac.manager.model.competition.CompetitionSeason
import javax.persistence._
import javax.persistence.JoinColumn

@Entity
@Table(name = "GAME")
class Game(leaSea: CompetitionSeason, gameDay: Int) {

  @Id
  @SequenceGenerator(name = "gameIdGenerator", sequenceName = "game_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "gameIdGenerator")
  var gameId: java.lang.Long = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns(Array(
    new JoinColumn(name = "competition.id"),
    new JoinColumn(name = "seasonSlug")
  ))
  var competitionSeason: CompetitionSeason = leaSea

  @BeanProperty
  @Column(nullable = false)
  var matchDay: Int = gameDay

  def this() = this(null, 0)

  override def toString = {
    "GameId: " + gameId +
      "\ncompetitionSeason: " + competitionSeason +
      "\ngameDay: " + matchDay
  }
}

