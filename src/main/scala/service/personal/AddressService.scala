package main.scala.service.personal

import main.scala.model.personal.Address

trait AddressService {

  def createAddress(country: String): Address
  
}