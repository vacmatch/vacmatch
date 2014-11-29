package main.scala.service.competition

import main.scala.model.team.Team
import main.scala.model.competition.Competition

trait CompetitionService {

  def findCompetitionsByFederationId(fedId: Long): List[Competition]

}
