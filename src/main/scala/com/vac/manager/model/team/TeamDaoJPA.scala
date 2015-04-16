package com.vac.manager.model.team

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import javax.persistence.Query
import com.vac.manager.model.competition.Competition
import com.vac.manager.model.generic.exceptions.NotImplementedException
import scala.collection.JavaConverters._

@Repository("teamDao")
class TeamDaoJPA
  extends GenericDaoJPA[Team, java.lang.Long](classOf[Team])
  with TeamDao {

  def findByTeamName(teamName: String): Team = {
    getEntityManager().createQuery(
      "SELECT t FROM Team t " +
        "WHERE t.teamName LIKE :teamName ")
      .setParameter("teamName", teamName)
      .getSingleResult()
      .asInstanceOf[Team]
  }

  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): Seq[Team] = {
    getEntityManager().createQuery(
      // TODO Modify this to return only teams in this federation
      "SELECT t FROM Team t ", classOf[Team]).getResultList().asScala
  }

  def findTeamsByCompetitionId(compId: Long, fedId: Long): List[Team] = {
    //TODO
    //Implementation
    null
  }

  def hasCompetitions(teamId: Long): Boolean = {
    var count: Long = getEntityManager().createQuery(
      "SELECT count(tc.compId) FROM TEAM t " +
        "JOIN TEAM_COMPETITION tc " +
        "ON t.teamId = tc.teamId" +
        "WHERE t.teamId = :teamId")
      .setParameter("teamId", teamId)
      .getFirstResult()

    count.>(0)
  }

  def getNumberByFederationId(fedId: Long): Long = {
    getEntityManager().createQuery(
      "SELECT count(t) FROM Team t " +
        "WHERE t.teamId IN ( SELECT tc.teamId " +
        "FROM COMPETITION c JOIN TEAM_COMPETITION tc " +
        "ON c.compId = tc.compId" +
        "WHERE c.fedId = :fedId )")
      .setParameter("fedId", fedId)
      .getFirstResult()
      .asInstanceOf[Long]
  }

}

