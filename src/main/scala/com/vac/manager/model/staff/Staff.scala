package com.vac.manager.model.staff

import javax.persistence._
import scala.beans.BeanProperty
import com.vac.manager.model.team.Team
import java.util.Calendar
import com.vac.manager.model.personal.Address
import scala.collection.JavaConverters._
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation

@Entity
@Table(name = "STAFF")
@Inheritance(strategy=InheritanceType.JOINED)
class Staff(stName: String,
    stSurnames: Seq[String],
    stEmail: String,
    stTelephones: Seq[String],
    stAddress: Address,
    stNif: String,
    stBirth: Calendar,
    stFederation: Federation) {

  @Id
  @SequenceGenerator(name="staffIdGenerator", sequenceName="staff_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffIdGenerator")
  var staffId: Long = _

  @BeanProperty
  @Column(nullable = false)
  var staffName: String = stName

  @BeanProperty
  @ElementCollection
  @CollectionTable
  @Column(nullable = false)
  var staffSurnames: java.util.List[String] = stSurnames.asJava

  @BeanProperty
  @Column(nullable = false)
  var staffActivated: Boolean = false

  @BeanProperty
  @Column(nullable = false)
  var staffPrivacityActivated: Boolean = false

  @BeanProperty
  @Column(nullable = false)
  var staffAlias: String = stName

  @BeanProperty
  @Column
  var staffEmail: String = stEmail

  @BeanProperty
  @Column
  var staffAvatarLink: String = Gravatar(stEmail).ssl(true).avatarUrl
  
  @BeanProperty
  @ElementCollection
  @CollectionTable
  @Column
  var staffTelephones: java.util.List[String] = stTelephones.asJava

  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "addressId")
  var staffAddress: Address = stAddress

  @BeanProperty
  @Column(nullable = false)
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
  var staffTeamList: java.util.List[Team] = _

  @BeanProperty
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fedId")
  var staffFederation: Federation = stFederation

  def this() = this(null, null, null, null, null, null, null, null)

  override
  def toString = "(" + this.staffId + ") " + this.staffSurnames +
                                        ", " + this.staffName +
                                        "\nNIF: " + this.staffNif +
                                        "\nEmail: " + this.staffEmail +
                                        "\nTeams: " + this.staffTeamList

}
