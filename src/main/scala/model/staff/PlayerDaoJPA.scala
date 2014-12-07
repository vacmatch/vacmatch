package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA
import java.util.Calendar
import main.scala.model.personal.Address

@Repository("playerDao")
class PlayerDaoJPA 
		extends GenericDaoJPA[Player,java.lang.Long](classOf[Player]) 
		with PlayerDao {

  

}


