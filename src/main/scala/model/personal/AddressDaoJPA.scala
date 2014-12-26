package main.scala.model.personal

import main.scala.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("addressDao")
class AddressDaoJPA 
	extends GenericDaoJPA[Address, java.lang.Long](classOf[Address])
	with AddressDao {

}