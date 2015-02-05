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

@Repository("staffDao")
class StaffDaoJPA
		extends GenericDaoJPA[Staff,java.lang.Long](classOf[Staff])
		with StaffDao {

  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Staff] = {
    null
  }

  def findAllByFederationId(fedId: Long): Seq[Staff] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Staff s " +
        "WHERE s.staffFederation.fedId = :fedId ", classOf[Staff]
    )
      .setParameter("fedId", fedId)

    query.getResultList().asScala
  }

  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Staff] = {
    null
  }

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff] = {
    null
  }

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff] = {
    null
  }
  
}


