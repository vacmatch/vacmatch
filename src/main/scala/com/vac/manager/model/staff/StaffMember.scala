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
@Table(name = "STAFFMEMBER")
@Inheritance(strategy=InheritanceType.JOINED)
class StaffMember(stName: String,
    stSurnames: String,
    stEmail: String,
    stTelephones: String,
    stAddress: Address,
    stNif: String,
    stBirth: Calendar,
    stFederation: Federation) {

  @Id
  @SequenceGenerator(name="staffMemberIdGenerator", sequenceName="staffMember_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "staffMemberIdGenerator")
  var staffId: Long = _

  @BeanProperty
  @Column(nullable = false)
  var staffName: String = stName

  @BeanProperty
  @Column(nullable = false)
  var staffSurnames: String = stSurnames

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
  @Column
  var staffTelephones: String = stTelephones

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

  def this() = this("", "", "", null, null, "", null, null)

  override
  def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[StaffMember])
      false
    var staffObj: StaffMember = obj.asInstanceOf[StaffMember]
    (staffObj.staffName == this.staffName) &&
    (staffObj.staffSurnames == this.staffSurnames) &&
    (staffObj.staffActivated == this.staffActivated) &&
    (staffObj.staffPrivacityActivated == this.staffPrivacityActivated) &&
    (staffObj.staffAlias == this.staffAlias) &&
    (staffObj.staffEmail == this.staffEmail) &&
    (staffObj.staffTelephones == this.staffTelephones) &&
    (staffObj.staffAddress == this.staffAddress) && 
    (staffObj.staffNif == this.staffNif) &&
    (staffObj.staffBirth == this.staffBirth) &&
    (staffObj.staffTeamList == this.staffTeamList) &&
    (staffObj.staffFederation == this.staffFederation)
  }
  
  override
  def toString = "(" + this.staffId + ") " + this.staffSurnames +
                                        ", " + this.staffName +
                                        "\nNIF: " + this.staffNif +
                                        "\nEmail: " + this.staffEmail +
                                        "\nTeams: " + this.staffTeamList

}
