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
    addressDao.findById(addressId)
  }

  def createAddress(firstLine: String, secondLine: String, postCode: String,
    locality: String, province: String, country: String): Address = {

    var address: Address = new Address(firstLine, secondLine, postCode, locality,
      province, country)
    addressDao.save(address)
    address
  }

  def removeAddress(addressId: Long): Boolean = {
    find(addressId).map {
      address =>
        addressDao.remove(address)
        true
    }.getOrElse(false)
  }

}

