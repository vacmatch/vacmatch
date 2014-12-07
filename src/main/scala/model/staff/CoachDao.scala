package main.scala.model.staff

import main.scala.model.generic.GenericDao
import main.scala.model.personal.Address
import java.util.Calendar

trait CoachDao extends GenericDao[Coach, java.lang.Long] {
  
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Coach]

  def findAll(startIndex: Int, count: Int): Seq[Coach]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Coach]
	
  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Coach]
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach]
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach]  
  
  def getCoachLicense(staffId: Long): License
  
}