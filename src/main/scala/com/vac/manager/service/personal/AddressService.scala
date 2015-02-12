package com.vac.manager.service.personal

import com.vac.manager.model.personal.Address

trait AddressService {
  
  def find(addressId: Long): Option[Address]
  
  def createAddress(addressLine: String, postCode: String,
      locality: String, province: String, country: String): Address
      
  def removeAddress(addressId: Long)

}