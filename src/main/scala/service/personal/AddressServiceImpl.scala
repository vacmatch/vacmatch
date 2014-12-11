package main.scala.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import main.scala.model.personal.Address

@Service("addressService")
@Transactional
class AddressServiceImpl extends AddressService {

  def createAddress(country: String): Address = {
    var address: Address = new Address(country)
    address
  }

}