package com.vac.manager.model.game

import com.vac.manager.model.team.Team
import scala.beans.BeanProperty
import java.lang.Long

class SoccerClassificationEntry(ass: Long, t: Team, p: Long, w: Long, d: Long, l: Long, gf: Long, ga: Long, gd: Long, pts: Long) {

  @BeanProperty
  var assessment: Long = ass

  @BeanProperty
  var team: Team = t

  @BeanProperty
  var played: Long = p

  @BeanProperty
  var won: Long = w

  @BeanProperty
  var drawn: Long = d

  @BeanProperty
  var lost: Long = l

  @BeanProperty
  var goalsFor: Long = gf

  @BeanProperty
  var goalsAgainst: Long = ga

  @BeanProperty
  var goalDifference: Long = gd

  @BeanProperty
  var points: Long = pts

  def this() = this(0, null, 0, 0, 0, 0, 0, 0, 0, 0)

  override def toString(): String =
    "\n SOCCER CLASSIFICATION ENTRY: " +
      "\n " + assessment +
      "\n " + team +
      "\n " + played +
      "\n " + won +
      "\n " + drawn +
      "\n " + lost +
      "\n " + goalsFor +
      "\n " + goalsAgainst +
      "\n " + goalDifference +
      "\n " + points

}
