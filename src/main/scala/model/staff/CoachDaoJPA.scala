package main.scala.model.staff

import org.springframework.stereotype.Repository
import javax.transaction.Transactional
import main.scala.model.generic.GenericDaoJPA

@Repository("coachDao")
class CoachDaoJPA 
		extends GenericDaoJPA[Coach,java.lang.Long](classOf[Coach]) 
		with CoachDao {
  
}