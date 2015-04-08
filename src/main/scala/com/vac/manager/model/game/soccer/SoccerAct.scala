package com.vac.manager.model.game.soccer

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.SequenceGenerator
import javax.persistence.Id
import javax.persistence.GeneratedValue
import java.util.Calendar
import com.vac.manager.model.team.Team
import scala.beans.BeanProperty
import javax.persistence.Temporal
import javax.persistence.ElementCollection
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import com.vac.manager.model.game.Game
import javax.persistence.GenerationType
import javax.persistence.FetchType
import javax.persistence.TemporalType

@Entity
@Table(name = "SOCCER_ACT")
class SoccerAct(g: Game) {

  @Id
  @SequenceGenerator(name = "soccerActIdGenerator", sequenceName = "soccerAct_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "soccerActIdGenerator")
  @BeanProperty
  var actId: Long = _

  @BeanProperty
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gameId")
  var game: Game = g

  @BeanProperty
  @Temporal(TemporalType.DATE)
  var date: Calendar = _

  @BeanProperty
  var location: String = _

  // TODO Create real referees
  @BeanProperty
  var referees: String = _

  // Local stadistics
  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "lTeamId")
  var localTeam: Team = _

  // Visitor stadistics
  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "vTeamId")
  var visitorTeam: Team = _

  @BeanProperty
  var incidents: String = _

  // TODO Create real signatures
  @BeanProperty
  var signatures: String = _

  def this() = this(null)

  override def toString(): String = {
    "{SOCCER ACT (" + actId + ")" +
      "\ndate: " + date +
      "\nlocation: " + location +
      "\nreferees: " + referees +
      "\nlocalTeam: " + localTeam +
      "\nvisitorTeam: " + visitorTeam +
      "\nincidents: " + incidents +
      "\nsignatures: " + signatures +
      "}"
  }

}

