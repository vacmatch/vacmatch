package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import java.util.Calendar
import com.vac.manager.model.personal.Address

@Repository("playerDao")
class PlayerDaoJPA
  extends StaffMemberDaoJPA
  with PlayerDao {

  override def findById(staffId: Long): Player = {
    var query = getEntityManager().createQuery(
      "SELECT p FROM Player p " +
        "WHERE p.staffId = :staffId ", classOf[Player])
      .setParameter("staffId", staffId)

    query.getSingleResult()
  }

  override def findByNameAndSurname(name: String, surname: String, startIndex: Int,
    count: Int): Seq[Player] = {
    null
  }

  override def findAllByFederationId(fedId: Long): Seq[Player] = {
    null
  }

  override def findAllByActivated(activated: Boolean, startIndex: Int,
    count: Int): Seq[Player] = {
    null
  }

  override def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player] = {
    null
  }

  override def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[Player] = {
    null
  }

}


