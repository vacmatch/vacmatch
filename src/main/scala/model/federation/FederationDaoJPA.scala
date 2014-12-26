package main.scala.model.federation

import main.scala.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("federationDao")
class FederationDaoJPA 
			extends GenericDaoJPA[Federation, java.lang.Long](classOf[Federation])
			with FederationDao{

}