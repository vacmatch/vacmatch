package main.scala.model.competition

import main.scala.model.generic.GenericDaoHibernate
import org.springframework.stereotype.Repository

@Repository("leagueDao")
class LeagueDaoJpa extends GenericDaoHibernate[League,java.lang.Long](classOf[League]) with LeagueDao {

}
