package com.vac.manager.model.competition

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.federation.Federation
import java.util.ArrayList

@Entity
@Table(name = "COMPETITION")
class Competition(compName: String, fed: Federation) {

  @Id
  @SequenceGenerator(name = "competitionIdGenerator", sequenceName = "competition_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "competitionIdGenerator")
  var compId: Long = _

  @BeanProperty
  @Column
  var competitionName: String = compName

  @BeanProperty
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "fedId")
  var federation: Federation = fed

  def this() = this(null, null)

  override def toString = "(" + this.compId + ") " + this.competitionName

}
