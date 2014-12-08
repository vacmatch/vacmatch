package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("staffDao")
class StaffDaoJPA 
		extends GenericDaoJPA[Staff,java.lang.Long](classOf[Staff])
		with StaffDao {
  
  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Staff] = {
    null
  }

  def findAll(startIndex: Int, count: Int): Seq[Staff] = {
    null
  }

  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Staff] = {
    null
  }

  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Staff] = {
    null
  }

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff] = {
    null
  }

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff] = {
    null
  }
  
}


