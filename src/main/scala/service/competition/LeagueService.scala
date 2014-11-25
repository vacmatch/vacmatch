package main.scala.service.competition

import java.util.Date
import main.scala.model.competition.League

trait LeagueService {
  def createLeague(fedId: Int, leagueName: String): League
  def findActiveByFederation(fedId: Int): Seq[League]
  def findActiveByFederation(fedId: Int, startTime: Date, endTime: Date): Seq[League]
  def findAllByFederation(fedId: Int): Seq[League]
  def findById(leagueId: Int): Option[League]
}
