package main.scala.model.staff

import main.scala.model.generic.GenericDao
import main.scala.model.personal.Address
import java.util.Calendar

trait PlayerDao extends StaffDao {
  
  def findByStaffId(staffId: Long, fedId: Long): Option[Player]

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Player]

  def findAllByFederationId(fedId: Long): Seq[Player]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Player]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player]
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Player]

}


