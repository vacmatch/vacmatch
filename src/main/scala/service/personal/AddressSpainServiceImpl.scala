package main.scala.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import main.scala.model.personal.AddressSpain
import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.personal.AddressSpainDao

@Service("addressSpainService")
@Transactional
class AddressSpainServiceImpl
	extends AddressServiceImpl
	with AddressSpainService{

  @Autowired
  var addressSpainDao: AddressSpainDao = _
  
  def createAddress(road: String, number: String, flat: String, postCode: Int,
      locality: String, province: String, country: String): AddressSpain = {
    
    var address: AddressSpain = new AddressSpain(road, number, flat, postCode,
        locality, province, country)
    addressSpainDao.save(address)
    address
  }

}



