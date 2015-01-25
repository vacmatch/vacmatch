package com.vac.manager.model.personal

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("addressDao")
class AddressDaoJPA 
	extends GenericDaoJPA[Address, java.lang.Long](classOf[Address])
	with AddressDao {

}