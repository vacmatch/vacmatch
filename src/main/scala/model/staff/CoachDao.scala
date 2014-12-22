package main.scala.model.staff

import main.scala.model.generic.GenericDao
import main.scala.model.personal.Address
import java.util.Calendar

trait CoachDao extends StaffDao {

  def findByStaffId(staffId: Long, fedId: Long): Option[Coach]

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Coach]

  def findAllByFederationId(fedId: Long): Seq[Coach]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Coach]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach]
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach]  
  
  def getCoachLicense(staffId: Long): License

}