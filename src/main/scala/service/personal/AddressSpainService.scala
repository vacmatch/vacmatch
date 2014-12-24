package main.scala.service.personal

import main.scala.model.personal.AddressSpain

trait AddressSpainService extends AddressService{
  
  def createAddress(road: String, number: String, flat: String, postCode: Int,
      locality: String, province: String, country: String): AddressSpain

}