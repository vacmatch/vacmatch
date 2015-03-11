package com.vac.manager.model.team

import javax.persistence._
import scala.beans.BeanProperty
import java.util.Calendar
import com.vac.manager.model.competition.Competition
import javax.persistence.metamodel.StaticMetamodel
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.StaffMember
import scala.collection.JavaConverters._
import java.util.ArrayList

@Entity
@Table(name = "TEAM")
class Team(name: String, publicName: String, date: Calendar, address: Address,
  web: String, telephones: java.util.List[String]) {

  @Id
  @SequenceGenerator(name = "teamIdGenerator", sequenceName = "team_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "teamIdGenerator")
  var teamId: java.lang.Long = _

  @BeanProperty
  @Column(nullable = false)
  var teamName: String = name

  @BeanProperty
  @Column(nullable = false)
  var teamActivated: Boolean = false

  @BeanProperty
  @Column(nullable = false)
  var publicTeamName: String = publicName

  @BeanProperty
  @Column
  @Temporal(TemporalType.TIMESTAMP)
  var foundationDate: Calendar = date

  @BeanProperty
  @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "addressId")
  var teamAddress: Address = address

  @BeanProperty
  @Column
  var teamWeb: String = web

  @BeanProperty
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable
  @JoinColumn
  var teamTelephones: java.util.List[String] = telephones

  @BeanProperty // TODO: Add @Column and model sponsors as a real thing
  // And remove @Transient
  // @Column
  @Transient
  var sponsorsList: java.util.List[String] = new ArrayList()

  @BeanProperty
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "staffTeam")
  var staffMemberList: java.util.List[StaffMember] = new ArrayList()

  @BeanProperty
  @ManyToMany(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinTable(
    name = "TEAM_COMPETITION",
    joinColumns =
      Array(new JoinColumn(name = "compId", nullable = false, updatable = false)),
    inverseJoinColumns =
      Array(new JoinColumn(name = "teamId", nullable = false, updatable = false)))
  var competitionsList: java.util.List[Competition] = new ArrayList()

  def this() = this(null, null, null, null, null, null)

  override def equals(obj: Any): Boolean = {
    Option(obj).flatMap(obj => {
      if (!obj.isInstanceOf[Team])
        None
      else
        Some(obj.asInstanceOf[Team])
    }).exists({ teamObj =>
      (teamObj.teamId == this.teamId) &&
        (teamObj.teamName == this.teamName) &&
        (teamObj.publicTeamName == this.publicTeamName) &&
        (teamObj.teamActivated == this.teamActivated) &&
        (teamObj.foundationDate == this.foundationDate) &&
        (teamObj.teamAddress == this.teamAddress) &&
        (teamObj.teamWeb == this.teamWeb) &&
        (teamObj.teamTelephones == this.teamTelephones) &&
        (teamObj.sponsorsList == this.sponsorsList) &&
        (teamObj.staffMemberList == this.staffMemberList) &&
        (teamObj.competitionsList == this.competitionsList)
    })
  }

  override def toString = "(" + this.teamId + ")" +
    "\nName: " + this.teamName +
    "\nPublicName: " + this.publicTeamName +
    "\nTeamActivated: " + this.teamActivated +
    "\nFundation: " + this.foundationDate +
    "\nAddress: " + this.teamAddress +
    "\nWeb: " + this.teamWeb +
    "\nTelephones: " + this.teamTelephones +
    "\nSponsorsList: " + this.sponsorsList +
    "\nStaffList: " + this.staffMemberList
  //"\nCompetitionsList: " + this.competitionsList

}
