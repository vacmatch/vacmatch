package main.scala.service.competition

import org.springframework.stereotype.Service
import main.scala.model.competition.Competition

@Service("competitionService")
class CompetitionServiceJPA extends CompetitionService {

    def findCompetitionsByFederationId(fedId: Long): List[Competition] = {
      null
    }

}