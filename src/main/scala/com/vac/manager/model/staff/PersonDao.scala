package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao

trait PersonDao extends GenericDao[Person, java.lang.Long] {

  def findByName(name: String, startIndex: Int, count: Int): Seq[Person]

  def findAllByFederationId(fedId: Long): Seq[Person]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Person]

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Person]

}