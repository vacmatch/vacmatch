package com.vac.manager.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.personal.Address
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.personal.AddressDao

@Service("addressService")
@Transactional
class AddressSpainServiceImpl
	extends AddressService {
  
  @Autowired
  var addressSpainDao: AddressDao = _
  
  def createAddress(road: String, number: String, flat: String, postCode: Int,
      locality: String, province: String, country: String): Address = {
    
    var address: Address = new Address(road, number, flat, postCode,
        locality, province, country)
    addressSpainDao.save(address)
    address
  }

}



