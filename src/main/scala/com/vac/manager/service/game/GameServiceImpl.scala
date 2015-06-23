package com.vac.manager.service.game

import org.springframework.stereotype.Service
import com.vac.manager.model.game.GameDao
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.game.Game
import com.vac.manager.service.competition.CompetitionService
import com.vac.manager.model.competition.CompetitionSeason
import javax.transaction.Transactional
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.game.soccer.SoccerActService
import com.vac.manager.model.game.soccer.SoccerClassificationEntry
import com.vac.manager.service.team.TeamService
import com.vac.manager.service.game.soccer.SoccerCompetitionStrategyService
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.competition.Sport
import com.vac.manager.model.game.ClassificationEntry

@Service("gameService")
@Transactional
class GameServiceImpl extends GameService {

  @Autowired
  var gameDao: GameDao = _

  @Autowired
  var competitionService: CompetitionService = _

  @Autowired
  var soccerActService: SoccerActService = _

  @Autowired
  var teamService: TeamService = _

  @Autowired
  var soccerCompetitionStrategy: SoccerCompetitionStrategyService = _

  @Autowired
  var federationService: FederationService = _

  def find(gameId: Long): Option[Game] = {
    gameDao.findById(gameId)
  }

  def findCompetitionCalendar(competitionSeason: CompetitionSeason): Seq[Game] = {
    gameDao.findAllBySeason(competitionSeason.id)
  }

  def createCompetitionCalendar(competitionSeason: CompetitionSeason, teamsNumber: Int, competitionRounds: Int): Seq[Game] = {
    competitionService.findSeasonByCompetitionSlug(
      competitionSeason.id.competition.fedId,
      competitionSeason.id.competition.slug,
      competitionSeason.id.seasonSlug
    ).map {
      season =>
        season.id.competition.sport match {
          case Sport.SOCCER =>
            soccerCompetitionStrategy.createCompetitionCalendar(competitionSeason, teamsNumber, competitionRounds)
        }
    }.getOrElse(List())
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def removeCompetitionCalendarFromSeason(competitionSeason: CompetitionSeason) = {
    competitionService.findSeasonByCompetitionSlug(
      competitionSeason.id.competition.fedId,
      competitionSeason.id.competition.slug,
      competitionSeason.id.seasonSlug
    ).map {
      season =>
        season.id.competition.sport match {
          case Sport.SOCCER =>
            soccerCompetitionStrategy.removeCompetitionCalendarFromSeason(competitionSeason)
        }
    }
  }

  def getCompetitionClassification(competitionSeason: CompetitionSeason): Seq[ClassificationEntry] = {
    competitionService.findSeasonByCompetitionSlug(
      competitionSeason.id.competition.fedId,
      competitionSeason.id.competition.slug,
      competitionSeason.id.seasonSlug
    ).map {
      season =>
        season.id.competition.sport match {
          case Sport.SOCCER =>
            soccerCompetitionStrategy.getCompetitionClassification(competitionSeason)
        }
    }.getOrElse(List())
  }

}

