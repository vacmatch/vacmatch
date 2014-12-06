package main.scala.model.competition

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("competitionDao")
class CompetitionDaoJPA 
		extends GenericDaoJPA[Competition,java.lang.Long](classOf[Competition])
		with CompetitionDao  {

}