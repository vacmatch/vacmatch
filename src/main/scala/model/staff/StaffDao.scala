package main.scala.model.staff

import main.scala.model.generic.GenericDao

trait StaffDao extends GenericDao[Staff, java.lang.Long]{

  def findByStaffId(staffId: Long, fedId: Long): Staff

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff]

  def findAllByFederationId(fedId: Long): Seq[Staff]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff]

}