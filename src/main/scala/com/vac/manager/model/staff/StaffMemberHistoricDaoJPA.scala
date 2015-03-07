package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import scala.collection.JavaConverters._
import com.vac.manager.model.team.Team

@Repository("staffMemberHistoricDao")
class StaffMemberHistoricDaoJPA
  extends GenericDaoJPA[StaffMemberHistoric, java.lang.Long](classOf[StaffMemberHistoric])
  with StaffMemberHistoricDao {

  def findActivatedList(teamId: Long): Seq[StaffMemberHistoric] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM StaffMemberHistoric s " +
        "WHERE s.exitDate IS EMPTY AND s.staffTeam.teamId = :teamId", classOf[StaffMemberHistoric])
      .setParameter("teamId", teamId)

    query.getResultList().asScala
  }

  def findActivatedElement(staffId: Long, teamId: Long): Option[StaffMemberHistoric] = {
    var result = getEntityManager().createQuery(
      "SELECT s FROM StaffMemberHistoric s " +
        "WHERE s.staffMember.staffId = :staffId " +
        "AND s.staffTeam.teamId = :teamId " +
        "AND s.exitDate IS NULL", classOf[StaffMemberHistoric])
      .setParameter("teamId", teamId)
      .setParameter("staffId", staffId)
      .getResultList()

    if (result.isEmpty()) {
      None
    } else {
      Some(result.get(0))
    }
  }

}

