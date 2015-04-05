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

@Entity
@Table(name = "SOCCER_STAFF_STATS")
class SoccerStaffStats {

  @Id
  @SequenceGenerator(name = "soccerStaffStatsIdGenerator", sequenceName = "soccerStaffStats_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "soccerStaffStatsIdGenerator")
  var statsId: Long = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actId")
  var act: SoccerAct = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "staffMemberId")
  var staffMember: StaffMember = _

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

  /*
  @BeanProperty
  @ElementCollection(fetch = FetchType.LAZY)
  @MapKey
  var goals: java.util.Map[String, Int] = new java.util.HashMap[String, Int]()
  */
}

