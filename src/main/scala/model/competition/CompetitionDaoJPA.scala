package main.scala.model.competition

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoHibernate

@Repository("competitionDao")
class CompetitionDaoJPA 
		extends GenericDaoHibernate[Competition,java.lang.Long](classOf[Competition])
		with CompetitionDao  {

}