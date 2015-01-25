package com.vac.manager.service.personal

import com.vac.manager.model.personal.AddressSpain

trait AddressSpainService extends AddressService{
  
  def createAddress(road: String, number: String, flat: String, postCode: Int,
      locality: String, province: String, country: String): AddressSpain

}