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

@Repository("personDao")
class PersonDaoJPA
    extends GenericDaoJPA[Person, java.lang.Long](classOf[Person])
    with PersonDao {

  def findByName(name: String): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE LOWER(s.name) LIKE LOWER(:name) OR LOWER(s.surname) LIKE LOWER(:name)", classOf[Person]
    )
      .setParameter("name", "%" + name + "%")

    query.getResultList().asScala
  }

  def findAllByFederationId(fedId: Long): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE s.federation.fedId = :fedId ", classOf[Person]
    )
      .setParameter("fedId", fedId)

    query.getResultList().asScala
  }

  def findByEmail(email: String): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE LOWER(s.email) LIKE LOWER(:email)", classOf[Person]
    )
      .setParameter("email", "%" + email + "%")

    query.getResultList().asScala
  }

  def findByCardId(cardId: String): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE LOWER(s.cardId) LIKE LOWER(:cardIdFirst) OR LOWER(s.cardId) LIKE LOWER(:cardIdSecond)", classOf[Person]
    )
      .setParameter("cardIdFirst", "%" + cardId)
      .setParameter("cardIdSecond", cardId + "%")

    query.getResultList().asScala
  }

}

