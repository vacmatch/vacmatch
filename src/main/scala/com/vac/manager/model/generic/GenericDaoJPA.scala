package com.vac.manager.model.generic

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
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import scala.beans.BeanProperty

abstract class GenericDaoJPA[T, K <: Serializable](entClass: Class[T]) extends GenericDao[T, K] {

  @BeanProperty
  @PersistenceContext
  var entityManager: EntityManager = _

  var entityClass: Class[T] = entClass

  /**
    * Find all objects from EntityClass table
    */
  def findAll(): Seq[T] = {
    val criteria: CriteriaQuery[T] = entityManager.getCriteriaBuilder createQuery entityClass

    criteria select (criteria from entityClass)

    val teamList = (entityManager createQuery criteria).getResultList

    return teamList.toList // return Scala types
  }

  /**
    * Save or update entity
    */
  def save(entity: T): T = {
    entityManager.persist(entity)
    entity
  }

  /**
    * Remove entity from entity table
    */
  def remove(entity: T) = {
    entityManager.remove(entity)
  }

  /**
    * Find entity by id
    */
  def findById(id: K): Option[T] = {
    val entity = entityManager.find(entityClass, id).asInstanceOf[T];

    Option(entity)
  }

}
