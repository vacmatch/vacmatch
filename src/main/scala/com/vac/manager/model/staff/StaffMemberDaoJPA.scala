package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import scala.collection.JavaConverters._
import com.vac.manager.model.team.Team

@Repository("staffMemberDao")
class StaffMemberDaoJPA
    extends GenericDaoJPA[StaffMember, java.lang.Long](classOf[StaffMember])
    with StaffMemberDao {

  def findCurrentStaffMemberListByTeam(teamId: Long): Seq[StaffMember] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM StaffMember s " +
        "WHERE s.exitDate IS NULL AND s.staffTeam.teamId = :teamId", classOf[StaffMember])
      .setParameter("teamId", teamId)

    query.getResultList().asScala
  }

  def find(teamId: Long, personId: Long): Option[StaffMember] = {
    var result = getEntityManager().createQuery(
      "SELECT s FROM StaffMember s " +
        "WHERE s.person.id = :personId " +
        "AND s.staffTeam.teamId = :teamId " +
        "AND s.exitDate IS NULL", classOf[StaffMember])
      .setParameter("teamId", teamId)
      .setParameter("personId", personId)
      .getResultList()

    if (result.isEmpty()) {
      None
    } else {
      Some(result.get(0))
    }
  }

}

