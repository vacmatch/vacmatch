package main.scala.model.team

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoHibernate
import javax.persistence.Query
import main.scala.model.staff.Staff
import main.scala.model.competition.Competition
import main.scala.model.generic.exceptions.NotImplementedException

@Repository("teamDao")
class TeamDaoJPA 
		extends GenericDaoHibernate[Team,java.lang.Long](classOf[Team])
		with TeamDao {

  @throws[NotImplementedException]
  def findTeamsByFederationId(fedId: Long, startIndex: Int, count: Int): List[Team] = {
    throw new NotImplementedException()
  }


  def findByTeamName(teamName: String): Team = {
	getEntityManager().createQuery(
	    "SELECT t FROM Team t " +
	"WHERE t.teamName LIKE :teamName ")
		.setParameter("teamName", teamName)
		.getSingleResult()
		.asInstanceOf[Team]
  }


  def getTeamSponsors(teamId: Long): List[String] = {
    getEntityManager().createQuery(
	    "SELECT t.sponsorsList FROM Team t " +
		"WHERE t.teamId LIKE :teamId ")
		.setParameter("teamId", teamId)
		.getResultList()
		.asInstanceOf[List[String]]
  }


  def getTeamStaff(teamId: Long): List[Staff] = {
    getEntityManager().createQuery(
	    "SELECT t.staffList FROM Team t " +
		"WHERE t.teamId LIKE :teamId ")
		.setParameter("teamId", teamId)
		.getResultList()
		.asInstanceOf[List[Staff]]
  }


  def getTeamCompetitions(teamId: Long): List[Competition] = {
    getEntityManager().createQuery(
	    "SELECT t.competitionsList FROM Team t " +
		"WHERE t.teamId LIKE :teamId ")
		.setParameter("teamId", teamId)
		.getResultList()
		.asInstanceOf[List[Competition]]
  }


  def hasCompetitions(teamId: Long): Boolean = {
    var count: Int = getEntityManager().createQuery(
      "SELECT count(t) FROM Team t" +
	"WHERE t.teamId = :teamId")
      .setParameter("teamId", teamId)
      .getFirstResult()

    count.equals(0)
  }

  @throws[NotImplementedException]
  def getNumberByFederationId(fedId: Long): Long = {
    throw new NotImplementedException()
  }

}
