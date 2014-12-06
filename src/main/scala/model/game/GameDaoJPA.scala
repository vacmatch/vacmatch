package main.scala.model.game

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("gameDao")
class GameDaoJPA extends GenericDaoJPA[Game,java.lang.Long](classOf[Game]) with GameDao {

}