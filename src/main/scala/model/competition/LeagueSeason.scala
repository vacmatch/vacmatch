package main.scala.model.competition

import javax.persistence._
import scala.beans.BeanProperty
import java.util.Calendar
import main.scala.model.staff.Staff

@Entity
@Table(name = "SEASONL")
class LeagueSeason extends Serializable {

  @EmbeddedId
  var id: LeagueSeason.LeagueSeasonPK = _

  @BeanProperty
  @Column(nullable=false)
  @Temporal(TemporalType.DATE)
  var startTime: Calendar = _

  @BeanProperty
  @Column
  @Temporal(TemporalType.DATE)
  var endTime: Calendar = _
}

object LeagueSeason {

  @Embeddable
  class LeagueSeasonPK extends Serializable {
    @BeanProperty
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="lid", referencedColumnName="league_id", nullable=false)
    var league: League = _

    @BeanProperty
    @Column(nullable=false)
    var seasonYear: String = _ // Something like: 2012, or maybe "XIII"
  }
}
