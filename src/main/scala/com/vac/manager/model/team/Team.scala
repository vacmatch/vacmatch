package com.vac.manager.model.team

import javax.persistence._
import scala.beans.BeanProperty
import java.util.Calendar
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.competition.Competition
import javax.persistence.metamodel.StaticMetamodel;

@Entity
@Table(name = "TEAM")
//@BatchSize(size=10)
class Team (name: String, date: Calendar) {

  @Id
  @SequenceGenerator(name="teamIdGenerator", sequenceName="team_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "teamIdGenerator")
  var teamId: Long = _

  @BeanProperty
  @Column
  var teamName: String = name

  @BeanProperty
  @Column
  @Temporal(TemporalType.TIMESTAMP)
  var fundationDate: Calendar = date

  @BeanProperty
  // TODO: Add @Column and model sponsors as a real thing
  // And remove @Transient
  // @Column
  @Transient
  var sponsorsList: java.util.List[String] = _

  @BeanProperty
  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinTable(
      name = "TEAM_STAFF",
      joinColumns =
	Array(new JoinColumn(name = "staffId", nullable = false, updatable = false)),
      inverseJoinColumns =
	Array(new JoinColumn(name = "teamId", nullable = false, updatable = false))
  )
  var staffList: java.util.List[Staff] = _

  @BeanProperty
  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinTable(
      name = "TEAM_COMPETITION",
      joinColumns =
	Array(new JoinColumn(name = "compId", nullable = false, updatable = false)),
      inverseJoinColumns =
	Array(new JoinColumn(name = "teamId", nullable = false, updatable = false))
  )
  var competitionsList: java.util.List[Competition] = _

  def this() = this (null, null)

  override
  def toString = "(" + this.teamId + ") " + this.teamName +
					"\nFundation: " + this.fundationDate

}
