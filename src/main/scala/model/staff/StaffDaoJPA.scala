package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA
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
    val cb: CriteriaBuilder = getEntityManager.getCriteriaBuilder
    var criteria: CriteriaQuery[Staff] = cb createQuery (classOf[Staff])

    val root: Root[Staff] = criteria from (classOf[Staff])
    val cond: Predicate =
      cb.and(
        Array(
          cb.equal(root get ("fedId"), fedId): Predicate
        ):_*
      )

    criteria = criteria select root where (Array(cond):_*)

    val query: TypedQuery[Staff] = getEntityManager createQuery criteria

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


