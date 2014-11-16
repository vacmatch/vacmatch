package main.scala.models.team

import org.springframework.stereotype.Repository
import main.scala.models.generic.GenericDaoHibernate

@Repository("teamDao")
class TeamDaoHibernate extends GenericDaoHibernate[Team,java.lang.Long](classOf[Team]) with TeamDao {

  def findByTeamName(teamName: String): Team = {
    var team: Team = getEntityManager().createQuery(
      "SELECT t FROM Team t" +
        "WHERE t.teamName = :teamName")
      .setParameter("teamName", teamName)
      .getFirstResult()
      .asInstanceOf[Team]

    team
  }

  def hasCompetitions(teamId: Int): Boolean = {
    var count: Int = getEntityManager().createQuery(
      "SELECT count(t) FROM Team t" +
        "WHERE t.teamId = :teamId")
      .setParameter("teamId", teamId)
      .getFirstResult()

    count.equals(0)
  }

}






