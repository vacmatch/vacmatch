package main.scala.models.generic

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

  def findAll(): List[T] = {
    var criteria: CriteriaQuery[T] = getEntityManager().getCriteriaBuilder().createQuery(entityClass)
    var root = criteria.from(entityClass)
    criteria.select(root)
    var teamList = getEntityManager().createQuery(criteria).getResultList()

    teamList.toList // return Scala types
  }

  def save(entity:T)= {
    //getEntityManager().getTransaction().begin();
    getEntityManager().persist(entity)
    //getEntityManager().getTransaction().commit();
  }

  def remove(entity:T) = {
    getEntityManager().remove(entity);
  }

  def findById(id: K):T = {
    getEntityManager().find(entityClass, id).asInstanceOf[T];
  }

}
