package com.vac.manager.model.competition

import java.util.Calendar
import javax.persistence._
import scala.beans.BeanProperty

@Embeddable
class LeagueSeasonPK extends Serializable {
  @BeanProperty
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "lid", referencedColumnName = "league_id", nullable = false)
  var league: League = _

  @BeanProperty
  @Column(nullable = false)
  var seasonSlug: String = _ // Something like: 2012, or maybe "XIII"
}

@Entity
@Table(name = "SEASONL")
class LeagueSeason extends Serializable {

  @EmbeddedId
  @BeanProperty
  var id: LeagueSeasonPK = _

  @BeanProperty
  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  var startTime: Calendar = _

  @BeanProperty
  @Column
  @Temporal(TemporalType.DATE)
  var endTime: Calendar = _
}
