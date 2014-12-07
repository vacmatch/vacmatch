package main.scala.model.staff

import main.scala.model.generic.GenericDao

trait StaffDao extends GenericDao[Staff, java.lang.Long]{
	
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff]

  def findAll(startIndex: Int, count: Int): Seq[Staff]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff]

  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Staff]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff]

}