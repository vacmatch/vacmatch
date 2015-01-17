package com.vac.manager.model.staff

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.team.Team

@Entity
@Table(name = "STAFF")
abstract class Staff(stName: String) {

  @Id
  @SequenceGenerator(name="staffIdGenerator", sequenceName="staff_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffIdGenerator")
  var staffId: Long = _

  @BeanProperty
  @Column
  var staffName: String = stName

  @BeanProperty
  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinTable(
      name = "TEAM_STAFF",
      joinColumns =
	Array(new JoinColumn(name = "teamId", nullable = false, updatable = false)),
      inverseJoinColumns =
	Array(new JoinColumn(name = "staffId", nullable = false, updatable = false))
  )
  var teamList: java.util.List[Team] = _

  def this() = this(null)

  override
  def toString = "(" + this.staffId + ") " + this.staffName +
					"\nTeams: " + this.teamList

}
