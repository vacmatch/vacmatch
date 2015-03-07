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
@Table(name = "STAFF_MEMBER_HISTORIC")
class StaffMemberHistoric(staff: StaffMember, team: Team) {

  @Id
  @SequenceGenerator(name = "staffHistoricIdGenerator", sequenceName = "staffHistoric_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffHistoricIdGenerator")
  var staffHistoricId: java.lang.Long = _
  
  @BeanProperty
  var joinDate: Calendar = Calendar.getInstance()
  
  @BeanProperty
  var exitDate: Calendar = _
  
  @BeanProperty
  var selected: Boolean = true
  
  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "staffId")
  var staffMember: StaffMember = staff

  @BeanProperty
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "teamId")
  var staffTeam: Team = team

  def this() = this(null, null)
}

