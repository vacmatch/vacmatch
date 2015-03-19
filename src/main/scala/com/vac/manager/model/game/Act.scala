package com.vac.manager.model.game

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.SequenceGenerator
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.model.competition.League
import scala.beans.BeanProperty
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.ElementCollection
import javax.persistence.FetchType
import javax.persistence.OneToOne
import javax.persistence.JoinColumn

@Entity
@Table(name = "ACT")
class Act {

  @Id
  @SequenceGenerator(name = "gameIdGenerator", sequenceName = "game_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "gameIdGenerator")
  var actId: Long = _

  @BeanProperty
  @Temporal(TemporalType.DATE)
  var date: Calendar = _

  @BeanProperty
  var location: String = _

  // TODO Create real referees
  @BeanProperty
  @ElementCollection(fetch = FetchType.EAGER)
  var referees: java.util.List[String] = _

  // TODO Create real statistics
  @BeanProperty
  var localStats: String = _

  // TODO Create real statistics
  @BeanProperty
  var visitorStats: String = _

  @BeanProperty
  var incidents: String = _

  // TODO Create real signatures
  @BeanProperty
  @ElementCollection(fetch = FetchType.EAGER)
  var signatures: java.util.List[String] = _

  @BeanProperty
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gameId")
  var game: Game = _

}

