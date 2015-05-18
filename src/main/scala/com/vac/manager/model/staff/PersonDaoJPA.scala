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

  def findByName(name: String, startIndex: Int,
    count: Int): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE s.name LIKE :name OR s.surname LIKE :name", classOf[Person]
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

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE s.email LIKE :email", classOf[Person]
    )
      .setParameter("email", "%" + email + "%")

    query.getResultList().asScala
  }

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Person] = {
    var query = getEntityManager().createQuery(
      "SELECT s FROM Person s " +
        "WHERE s.cardId LIKE :cardIdFirst OR s.cardId LIKE :cardIdSecond", classOf[Person]
    )
      .setParameter("cardIdFirst", "%" + cardId)
      .setParameter("cardIdSecond", cardId + "%")

    query.getResultList().asScala
  }

}

