package main.scala.model.personal

import org.springframework.stereotype.Repository

@Repository("addressSpainDao")
class AddressSpainDaoJPA 
	extends AddressDaoJPA
	with AddressSpainDao {

}