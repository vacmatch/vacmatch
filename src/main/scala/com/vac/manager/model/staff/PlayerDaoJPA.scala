package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA
import java.util.Calendar
import com.vac.manager.model.personal.Address

@Repository("playerDao")
class PlayerDaoJPA 
		extends StaffDaoJPA
		with PlayerDao {

  override
  def findByStaffId(staffId: Long, fedId: Long): Option[Player] = {
    null
  }

  override
  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Player] = {
    null
  }

  override
  def findAllByFederationId(fedId: Long): Seq[Player] = {
    null
  }
  
  override
  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Player] = {
    null
  }

  override
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player] = {
    null
  }
	
  override
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Player]  = {
    null
  }

}


