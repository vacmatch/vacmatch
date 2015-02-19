package com.vac.manager.model.competition

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.team.Team
import com.vac.manager.model.federation.Federation


@Entity
@Table(name = "COMPETITION")
class Competition (compName: String, fed: Federation) {

  @Id
  @SequenceGenerator(name="competitionIdGenerator", sequenceName="competition_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "competitionIdGenerator")
  var compId: Long = _

  @BeanProperty
  @Column
  var competitionName: String = compName

  @BeanProperty
  @ManyToOne(optional=false, fetch = FetchType.LAZY)
  @JoinColumn(name = "fedId")
  var federation: Federation = fed
  
  @BeanProperty
  @ManyToMany(fetch = FetchType.LAZY, mappedBy="competitionsList", cascade = Array(CascadeType.ALL))
  var teamList: java.util.List[Team] = _

  def this() = this(null, null)

  override
  def toString = "(" + this.compId + ") " + this.competitionName +
					"\nTeams: " + this.teamList

}
