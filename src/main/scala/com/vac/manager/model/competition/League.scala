package com.vac.manager.model.competition

import java.util.Calendar
import javax.persistence._
import scala.beans.BeanProperty
import javax.validation.constraints.Size
import javax.validation.constraints.NotNull

@Entity
@Table(
  name = "LEAGUE",
  uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("fedId", "slug")))
) //@BatchSize(size=10)
class League {

  @Id
  @Column(name = "league_id")
  @SequenceGenerator(name = "leagueIdGenerator", sequenceName = "league_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "leagueIdGenerator")
  @BeanProperty
  var leagueId: java.lang.Long = _

  @BeanProperty
  @Column(nullable = false) // TODO: Actually relate to real federations
  var fedId: java.lang.Long = _

  @BeanProperty
  @NotNull
  @Size(min = 1)
  @Column(nullable = false)
  var leagueName: String = _

  @BeanProperty
  @NotNull
  @Size(min = 1)
  @Column(nullable = false)
  var slug: String = _

  @BeanProperty
  @Column
  @Temporal(TemporalType.DATE)
  var startDate: Calendar = _

  @BeanProperty
  @Column
  @Temporal(TemporalType.DATE)
  var endDate: Calendar = _

  @BeanProperty // TODO: Create sponsor things
  @Transient
  var sponsorList: java.util.List[String] = _

  @BeanProperty
  @OneToMany(mappedBy = "id.league", fetch = FetchType.EAGER)
  @OrderBy("startTime DESC")
  var seasonList: java.util.List[LeagueSeason] = _

  override def toString: String = {
    "main.scala.model.competition.League{fedId=" + (if (fedId != null) { fedId.toString() } else { "null" }) + ", name=" + leagueName + "}"
  }
}
