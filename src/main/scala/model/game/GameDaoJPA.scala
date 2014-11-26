package main.scala.model.game

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoHibernate

@Repository("gameDao")
class GameDaoJPA extends GenericDaoHibernate[Game,java.lang.Long](classOf[Game]) with GameDao {

}