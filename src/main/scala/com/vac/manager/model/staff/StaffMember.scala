package com.vac.manager.model.staff

import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import scala.beans.BeanProperty
import javax.persistence.GenerationType
import java.util.Calendar
import javax.persistence.ManyToOne
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import com.vac.manager.model.team.Team
import javax.persistence.Id
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "STAFF_MEMBER")
class StaffMember(per: Person, team: Team) {

  @Id
  @SequenceGenerator(name = "staffMemberIdGenerator", sequenceName = "staffMember_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffMemberIdGenerator")
  var staffMemberId: java.lang.Long = _

  @BeanProperty
  var joinDate: Calendar = Calendar.getInstance()

  @BeanProperty
  var exitDate: Calendar = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "personId")
  var person: Person = per

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "teamId")
  var staffTeam: Team = team

  def this() = this(null, null)
}

