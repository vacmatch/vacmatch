package com.vac.manager.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.personal.Address
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.personal.AddressDao

@Service("addressService")
@Transactional
class AddressServiceImpl
	extends AddressService {
  
  @Autowired
  var addressDao: AddressDao = _
  
  def find(addressId: Long): Option[Address] = {
    Option(addressDao.findById(addressId))
  }

  def createAddress(addressLine: String, postCode: String,
      locality: String, province: String, country: String): Address = {
    
    var address: Address = new Address(addressLine, postCode, locality,
        province, country)
    addressDao.save(address)
    address
  }

  def removeAddress(addressId: Long) = {
    
    val maybeAddress: Option[Address] = find(addressId)
    
    maybeAddress match {
      case None =>
      case Some(address) => addressDao.remove(address)
    }
  }
  
}



