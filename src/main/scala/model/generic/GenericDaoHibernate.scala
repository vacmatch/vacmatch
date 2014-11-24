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

abstract class GenericDaoHibernate[T, K <: Serializable](entityClass: Class[T]) extends GenericDao[T, K]{

  var entityManager: EntityManager = _

  var entityManagerFactory: EntityManagerFactory = _

  def getEntityManager(): EntityManager = {
	this.entityManager
  }

  @Autowired
  def setEntityManagerFactory(em: EntityManagerFactory) = {
    this.entityManagerFactory = em
    this.entityManager  = em.createEntityManager()
  }

  /**
   * Find all objects from EntityClass table
   */
  def findAll(): List[T] = {
    getEntityManager().getTransaction().begin();

    var criteria: CriteriaQuery[T] = getEntityManager().getCriteriaBuilder().createQuery(entityClass)
    var root = criteria.from(entityClass)
    criteria.select(root)
    var teamList = getEntityManager().createQuery(criteria).getResultList()

    getEntityManager().getTransaction().commit();

    teamList.toList // return Scala types
  }


  /**
   * Save or update entity
   */
  def save(entity:T)= {
    getEntityManager().getTransaction().begin();

    getEntityManager().persist(entity)

    getEntityManager().getTransaction().commit();
  }


  /**
   * Remove entity from entity table
   */
  def remove(entity:T) = {
    getEntityManager().getTransaction().begin();

    getEntityManager().remove(entity);

    getEntityManager().getTransaction().commit();
  }


  /**
   * Find entity by id
   */
  def findById(id: K):T = {
    getEntityManager().getTransaction().begin();

    var res = getEntityManager().find(entityClass, id).asInstanceOf[T];

    getEntityManager().getTransaction().commit();

    res
  }

}
