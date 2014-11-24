package main.scala.model.competition

import javax.persistence._
import scala.beans.BeanProperty
import main.scala.model.team.Team

@Entity
@Table(name = "COMPETITION")
class Competition (compName: String) {
  
  @Id
  @SequenceGenerator(name="competitionIdGenerator", sequenceName="competition_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "competitionIdGenerator")
  var compId: Long = _

  @BeanProperty
  @Column
  var competitionName: String = compName
  
  @BeanProperty
  @Column
  @ManyToMany(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinTable(
      name = "TEAM_COMPETITION",
      joinColumns = 
        Array(new JoinColumn(name = "teamId", nullable = false, updatable = false)),
      inverseJoinColumns =
        Array(new JoinColumn(name = "compId", nullable = false, updatable = false))
  )  
  var teamList: List[Team] = _
  
  def this() = this(null)
  
  override 
  def toString = "(" + this.compId + ") " + this.competitionName +
  					"\nTeams: " + this.teamList

}


