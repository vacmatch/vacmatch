package main.scala.model.generic

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import scala.collection.JavaConversions._
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceUnit
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.hibernate.Session
import java.io.Serializable
import javax.persistence.EntityManagerFactory
import org.springframework.stereotype.Service
import org.springframework.stereotype.Repository
import javax.persistence.metamodel.Metamodel
import main.scala.model.generic.exceptions.InstanceNotFoundException
import scala.beans.BeanProperty

abstract class GenericDaoHibernate[T, K <: Serializable](entityClass: Class[T]) extends GenericDao[T, K] {

  @BeanProperty
  @PersistenceContext
  var entityManager: EntityManager = _

  /**
    * Find all objects from EntityClass table
    */
  def findAll(): List[T] = {
    var criteria: CriteriaQuery[T] = getEntityManager().getCriteriaBuilder().createQuery(entityClass)
    var root = criteria.from(entityClass)
    criteria.select(root)
    var teamList = getEntityManager().createQuery(criteria).getResultList()

    teamList.toList // return Scala types
  }

  /**
    * Save or update entity
    */
  def save(entity: T) = {
    getEntityManager().persist(entity)
  }

  /**
    * Remove entity from entity table
    */
  def remove(entity: T) = {
    getEntityManager().remove(entity);
  }

  /**
    * Find entity by id
    */
  def findById(id: K): T = {
    var entity: T = getEntityManager().find(entityClass, id).asInstanceOf[T];

    if (entity == null) {
      throw new InstanceNotFoundException(id.toString(), entityClass.getName())
    }

    entity
  }

}
