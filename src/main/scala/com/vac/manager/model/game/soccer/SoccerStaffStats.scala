package com.vac.manager.model.game.soccer

import com.vac.manager.model.staff.StaffMember
import scala.beans.BeanProperty
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.Column
import javax.persistence.JoinColumn
import java.util.Calendar
import javax.persistence.ManyToOne
import javax.persistence.GenerationType
import javax.persistence.FetchType
import java.util.ArrayList
import javax.persistence.ElementCollection
import javax.persistence.Transient

@Entity
@Table(name = "SOCCER_STAFF_STATS")
class SoccerStaffStats(soccerAct: SoccerAct, staff: StaffMember) {

  @Id
  @SequenceGenerator(name = "soccerStaffStatsIdGenerator", sequenceName = "soccerStaffStats_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "soccerStaffStatsIdGenerator")
  @BeanProperty
  var statsId: java.lang.Long = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actId")
  var act: SoccerAct = soccerAct

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "staffMemberId")
  var staffMember: StaffMember = staff

  @BeanProperty
  @Column
  var isStaff: Boolean = false

  @BeanProperty
  @Column
  var staffPosition: String = _

  @BeanProperty
  @Column
  var isCalledUp: Boolean = false

  // The card's timestamp
  @BeanProperty
  @Column
  var firstYellowCard: Calendar = _

  // The card's timestamp
  @BeanProperty
  @Column
  var secondYellowCard: Calendar = _

  // The card's timestamp
  @BeanProperty
  @Column
  var redCard: Calendar = _

  @BeanProperty
  @ElementCollection(fetch = FetchType.EAGER)
  @Column
  var goals: java.util.List[Calendar] = new ArrayList[Calendar]()

  @BeanProperty
  def getGoalsNumber(): Integer = goals.size()

  def this() = this(null, null)

  override def toString(): String = {
    "{SOCCER STAFF STATS (" + statsId + ")" +
      //"\nact: " + act +
      "\nstaffMember: " + staffMember +
      "\nfirstYellowCard: " + firstYellowCard +
      "\nsecondYellowCard: " + secondYellowCard +
      "\nredCard: " + redCard +
      "}"
  }

}

