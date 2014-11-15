package main.scala.services

import main.scala.models.team.Team

trait TeamService {

  def createTeam(obj: Team)
  
  def getAllTeams(): Iterator[List[Team]]
  
}