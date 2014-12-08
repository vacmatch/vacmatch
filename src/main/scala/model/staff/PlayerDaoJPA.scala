package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA
import java.util.Calendar
import main.scala.model.personal.Address

@Repository("playerDao")
class PlayerDaoJPA 
		extends GenericDaoJPA[Player,java.lang.Long](classOf[Player]) 
		with PlayerDao {

  
  def findByNameAndSurname(name: String, surname: String, startIndex: Int,
      count: Int): Seq[Player] = {
    null
  }

  def findAll(startIndex: Int, count: Int): Seq[Player] = {
    null
  }

  def findAllByActivated(activated: Boolean, startIndex: Int,
      count: Int): Seq[Player] = {
    null
  }
	
  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Player] = {
    null
  }
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player] = {
    null
  }
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Player]  = {
    null
  }

}


