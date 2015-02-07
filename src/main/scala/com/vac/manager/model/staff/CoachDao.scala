package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao
import com.vac.manager.model.personal.Address
import java.util.Calendar

trait CoachDao extends StaffDao {

  def findById(staffId: Long): Coach

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Coach]

  def findAllByFederationId(fedId: Long): Seq[Coach]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Coach]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach]
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach]  
  
}