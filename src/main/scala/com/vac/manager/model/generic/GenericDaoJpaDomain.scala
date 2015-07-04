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

/**
  * This class encapsulates the inner "entity" classes of traditional ORMs so that
  * the actual domain classes can be case classes or anything more compact and manageable.
  * The three type parameters are:
  * a) T: The domain object type (nemo.: En_T_ity)
  * b) H: the entity object type (nemo.: _H_ibernate)
  * c) K: the domain object's primary key (nemo.: primary _K_ey)
  *
  * IMPORTANT: If the primary key datatype does not hold an equality
  * relationship on both entities, YOU MUST OVERRIDE findById in your
  * concrete implementation class to take care of the conversion.
  */
abstract class GenericDaoJPADomain[T <: AnyRef, H <: AnyRef, K](entClass: Class[H])(implicit ev1: Null <:< T, ev2: Null <:< H) extends GenericDao[T, K] {

  @BeanProperty
  @PersistenceContext
  var entityManager: EntityManager = _

  var entityClass: Class[H] = entClass

  def fromDomain(direct: T): H
  def toDomain(entity: H): T

  implicit def implicitlyFromDomain(direct: T): H = Option(direct).map(fromDomain).orNull
  implicit def implicitlyToDomain(entity: H): T = Option(entity).map(toDomain).orNull

  implicit def toDomainSeq(entity: Seq[H]): Seq[T] = entity.map(toDomain)
  implicit def toDomainOption(entity: Option[H]): Option[T] = entity.map(toDomain)

  /**
    * Find all objects from EntityClass table
    */
  def findAll(): Seq[T] = {
    _findAll // These functions all implicitly convert the inner type
    // to the outer type. This way the underscore functions
    // can be composed inside the DAO and only get one
    // implicit conversion instead of n.
  }

  /**
    * Save or update entity
    */
  def save(entity: T): T = _save(entity)

  /**
    * Remove entity from entity table
    */
  def remove(entity: T) = _remove(entity)

  /**
    * Find entity by id. Redefine if K is not the same in domain and
    * entity.
    */
  def findById(id: K): Option[T] = {
    _findById(id)
  }

  protected def _findAll(): Seq[H] = {
    val criteria: CriteriaQuery[H] = entityManager.getCriteriaBuilder createQuery entityClass

    criteria select (criteria from entityClass)

    val teamList = (entityManager createQuery criteria).getResultList

    teamList.toList // return Scala types (then implicitly converted to domain entities)
  }

  protected def _save(entity: H): H = {
    entityManager.persist(entity)
    entity
  }

  protected def _remove(entity: H) = entityManager.remove(entity)

  protected def _findById(id: K): Option[H] = {
    val entity = entityManager.find(entityClass, id)

    val r: Option[H] = Option(entity)

    r // Here we show the power of the implicit conversion
  }

}
