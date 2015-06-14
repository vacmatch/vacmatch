package com.vac.manager.model.competition

import java.util.Calendar
import javax.persistence._
import scala.beans.BeanProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import javax.validation.Valid

@Embeddable
class CompetitionSeasonPK extends Serializable {
  @BeanProperty
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "lid", referencedColumnName = "competition_id", nullable = false)
  var competition: Competition = _

  @BeanProperty
  @NotNull
  @Size(min = 1)
  @Column(nullable = false)
  var seasonSlug: String = _ // Something like: 2012, or maybe "XIII"

  override def toString() = {
    "\n-Competition: " + competition +
      "\n-seasonSlug: " + seasonSlug
  }
}

@Entity
@Table(name = "SEASONL")
class CompetitionSeason extends Serializable {

  @EmbeddedId
  @Valid
  @BeanProperty
  var id: CompetitionSeasonPK = _

  @BeanProperty
  @NotNull
  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  var startTime: Calendar = _

  @BeanProperty
  @Column
  @Temporal(TemporalType.DATE)
  var endTime: Calendar = _

  override def toString() = {
    "CompetitionSeason:" +
      "\nId: " + id +
      "\nStartTime(Calendar): " + startTime +
      "\nEndTime(Calendar): " + endTime

  }
}
