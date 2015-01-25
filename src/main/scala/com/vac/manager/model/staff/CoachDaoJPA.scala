package com.vac.manager.model.staff

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA

@Repository("coachDao")
class CoachDaoJPA
		extends StaffDaoJPA 
		with CoachDao {

  override
  def findByStaffId(staffId: Long, fedId: Long): Option[Coach] = {
    null
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
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
  
  def getCoachLicense(staffId: Long): License = {
    null
  }
  
  
}


