package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import javax.persistence.criteria.Predicate
import scala.collection.JavaConverters._
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import javax.persistence.TypedQuery
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.Metamodel

@Repository("staffMemberDao")
class StaffMemberDaoJPA
  extends GenericDaoJPA[StaffMember, java.lang.Long](classOf[StaffMember])
  with StaffMemberDao {

  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
    count: Int): Seq[StaffMember] = {
    null
  }

  def findAllByFederationId(fedId: Long): Seq[StaffMember] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM StaffMember s " +
        "WHERE s.staffFederation.fedId = :fedId ", classOf[StaffMember])
      .setParameter("fedId", fedId)

    query.getResultList().asScala
  }

  def findAllByActivated(activated: Boolean, startIndex: Int,
    count: Int): Seq[StaffMember] = {
    null
  }

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    null
  }

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    null
  }

}


