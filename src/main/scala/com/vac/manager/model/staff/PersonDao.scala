package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao

trait PersonDao extends GenericDao[Person, java.lang.Long] {

  def findByName(name: String): Seq[Person]

  def findAllByFederationId(fedId: Long): Seq[Person]

  def findByEmail(email: String): Seq[Person]

  def findByCardId(cardId: String): Seq[Person]

}