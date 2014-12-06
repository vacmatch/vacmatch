package com.vac.manager.model.staff

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.team.Team
import java.util.Calendar
import main.scala.model.personal.Avatar
import main.scala.model.personal.Address

@Entity
@Table(name = "STAFF")
@Inheritance(strategy=InheritanceType.JOINED)
class Staff(stName: String,
    stSurnames: java.util.List[String],
    stEmail: String,
    stTelephones: java.util.List[String],
    stAddress: Address,
    stNif: String,
    stBirth: Calendar) {

  @Id
  @SequenceGenerator(name="staffIdGenerator", sequenceName="staff_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffIdGenerator")
  var staffId: Long = _

  @BeanProperty
  @Column
  var staffName: String = stName

  @BeanProperty
  @Column
  var staffSurnames: java.util.List[String] = stSurnames

  @BeanProperty
  @Column
  var staffActivated: Boolean = false

  @BeanProperty
  @Column
  var staffPrivacityActivated: Boolean = false

  @BeanProperty
  @Column
  var staffAlias: String = stName

  @BeanProperty
  @OneToOne(optional=false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "avatarId")
  var staffAvatar: Avatar = _

  @BeanProperty
  @Column
  var staffEmail: String = stEmail

  @BeanProperty
  @Column
  var staffTelephones: java.util.List[String] = stTelephones

  @BeanProperty
  @Column
  var staffAddress: Address = stAddress

  @BeanProperty
  @Column
  var staffNif: String = stNif

  @BeanProperty
  @Column
  var staffBirth: Calendar = stBirth

  @BeanProperty
  @Column
  @ManyToMany(fetch = FetchType.LAZY,
      cascade = Array(CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE))
  @JoinTable(
      name = "TEAM_STAFF",
      joinColumns =
        Array(new JoinColumn(name = "teamId", nullable = false, updatable = false)),
      inverseJoinColumns =
        Array(new JoinColumn(name = "staffId", nullable = false, updatable = false))
  )
  var teamList: java.util.List[Team] = _

  def this() = this(null, null, null, null, null, null, null)

  override
  def toString = "(" + this.staffId + ") " + this.staffSurnames +
                                        ", " + this.staffName +
                                        "\nNIF: " + this.staffNif +
                                        "\nEmail: " + this.staffEmail +
                                        "\nTeams: " + this.teamList

}
