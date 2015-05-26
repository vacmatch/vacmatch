package com.vac.manager.model.competition

import javax.persistence.Table
import javax.persistence.Entity
import com.vac.manager.model.team.Team
import java.util.Calendar
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import scala.beans.BeanProperty
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import javax.persistence.JoinColumns

@Entity
@Table(name = "COMPETITION_MEMBER")
class CompetitionMember(start: Calendar, league: LeagueSeason, te: Team) {

  @Id
  @SequenceGenerator(name = "competitionMemberIdGenerator", sequenceName = "competitionMember_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "competitionMemberIdGenerator")
  var id: java.lang.Long = _

  @BeanProperty
  var startDate: Calendar = start

  @BeanProperty
  var endDate: Calendar = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumns(Array(
    new JoinColumn(name = "league.id"),
    new JoinColumn(name = "seasonSlug")
  ))
  var leagueSeason: LeagueSeason = league

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "teamId")
  var team: Team = te

  def this() = this(null, null, null)

}