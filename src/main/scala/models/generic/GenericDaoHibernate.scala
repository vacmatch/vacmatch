package main.scala.models.generic

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery
import scala.collection.JavaConversions._
import javax.persistence.PersistenceContext
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.hibernate.Session
import java.io.Serializable
import javax.persistence.EntityManagerFactory
import org.springframework.stereotype.Service

abstract class GenericDaoHibernate[T, K <: Serializable] extends GenericDao[T, K]{

  var entityClass: Class[T] = _

  @PersistenceContext
  var entityManager: EntityManager = _

  def getManager(): EntityManager = {
	this.entityManager
  }
  
  def setManager(em: EntityManagerFactory) = {
    this.entityManager  = em.createEntityManager()
  }
  
  def findAll(): List[T] = {
    var criteria: CriteriaQuery[T] = getManager().getCriteriaBuilder().createQuery(entityClass)
    var root = criteria.from(entityClass)
    criteria.select(root)
    var teamList: List[T] = getManager().createQuery(criteria).getResultList().toList

    teamList
  }
  
  def save(entity:T)= {
    //getManager().getTransaction().begin();
    getManager().persist(entity)
    //getManager().getTransaction().commit();
  }

  def remove(entity:T) = {
    getManager().remove(entity);        
  }

  def findById(id: K):T = {
    getManager().find(entityClass, id).asInstanceOf[T];
  }
      
}




