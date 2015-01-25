package com.vac.manager.service.personal

import com.vac.manager.model.personal.Address

trait AddressService {

  def createAddress(country: String): Address
  
}