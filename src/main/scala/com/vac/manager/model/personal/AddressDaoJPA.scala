package com.vac.manager.model.personal

import org.springframework.stereotype.Repository
import com.vac.manager.model.generic.GenericDaoJPA

@Repository("addressDao")
class AddressDaoJPA
    extends GenericDaoJPA[Address, java.lang.Long](classOf[Address])
    with AddressDao {

}