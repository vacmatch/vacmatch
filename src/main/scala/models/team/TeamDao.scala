package main.scala.models.team

import main.scala.models.generic.GenericDao

trait TeamDao extends GenericDao[Team, java.lang.Long]{
  
  def findByTeamName(name: String): Team
  
  def hasCompetitions(id: Int): Boolean
  
}