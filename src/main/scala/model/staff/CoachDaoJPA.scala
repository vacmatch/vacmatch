package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("coachDao")
class CoachDaoJPA 
		extends GenericDaoJPA[Coach,java.lang.Long](classOf[Coach]) 
		with CoachDao {
  
  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Coach] = {
    null
  }

  def findAll(startIndex: Int, count: Int): Seq[Coach] = {
    null
  }

  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Coach] = {
    null
  }
	
  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach] = {
    null
  }
  
  def getCoachLicense(staffId: Long): License = {
    null
  }
  
  
}


