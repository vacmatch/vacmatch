package main.scala.models.team

import javax.persistence._
import scala.beans.BeanProperty
import java.util.Calendar

@Entity
@Table(name = "TEAM")
//@BatchSize(size=10)
class Team (name: String, sponsor: String, staff: List[String], 
    competitions: List[String], date: Calendar) {

  @Id
  @SequenceGenerator(name="teamIdGenerator", sequenceName="team_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teamIdGenerator")
  var teamId: Int = _
 
  @BeanProperty
  @Column
  var teamName: String = name

  @BeanProperty
  @Column
  var sponsorName: String = sponsor

  @BeanProperty
  @Column
  @Temporal(TemporalType.TIMESTAMP)
  var fundationDate: Calendar = date
  
//  @BeanProperty
//  @Column
//  var staffList: List[String] = staff
//  @ManyToMany(optional = true, fetch = FetchType.LAZY)
//  @JoinColumn(name = "staffId")
  
//  @BeanProperty
//  @Column
//  var competitionsList: List[String] = competitions
//  @ManyToMany(optional = true, fetch = FetchType.LAZY)
//  @JoinColumn(name = "compId")
  
  def this() = this (null, null, null, null, null)
  
  override 
  def toString = "(" + this.teamId + ") " + this.teamName  + "\nSponsor: " +
		  this.sponsorName + "\nFundation: " + this.fundationDate
  
}


