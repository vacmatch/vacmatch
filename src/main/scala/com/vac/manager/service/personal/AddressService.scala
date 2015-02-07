package com.vac.manager.service.personal

import com.vac.manager.model.personal.Address

trait AddressService {
  
  def createAddress(road: String, number: String, flat: String, postCode: Int,
      locality: String, province: String, country: String): Address

}