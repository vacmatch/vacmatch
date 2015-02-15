package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA

@Repository("coachDao")
class CoachDaoJPA
		extends StaffMemberDaoJPA 
		with CoachDao {

  override
  def findById(staffId: Long): Coach = {
    var query = getEntityManager().createQuery(
      "SELECT c FROM Coach c " +
        "WHERE c.staffId = :staffId ", classOf[Coach]
    )
      .setParameter("staffId", staffId)

    query.getSingleResult()
  }

  override
  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Coach] = {
    null
  }

  override
  def findAllByFederationId(fedId: Long): Seq[Coach] = {
    null
  }

  override
  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Coach] = {
    null
  }
  
  override
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
  
  override
  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
  
}


