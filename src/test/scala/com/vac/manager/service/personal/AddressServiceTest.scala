package com.vac.manager.service.personal

import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.mock.MockitoSugar
import org.scalatest.BeforeAndAfter
import com.vac.manager.model.personal.Address
import org.scalatest.Matchers._
import org.mockito.Mockito._
import org.mockito.Mockito
import com.vac.manager.model.personal.AddressDao

class AddressServiceTest
  extends FeatureSpec
  with GivenWhenThen
  with MockitoSugar
  with BeforeAndAfter {

  /* Generic variables */
  var addressService: AddressServiceImpl = _
  var validAddress: Address = _

  before {
    //Initialization from service to be tested
    addressService = new AddressServiceImpl()
    addressService.addressDao = mock[AddressDao]
    validAddress = new Address("Plaza Pontevedra", "22", "15003", "A Coruña",
      "A Coruña", "España")
  }

  feature("Address creation") {
    scenario("An Address can be created") {

      Given("A new Address")
      val newAddress: Address = new Address("Plaza Pontevedra", "22", "15003",
        "A Coruña", "A Coruña", "España")

      When("Address try to be created")
      val createdAddress: Address =
        addressService.createAddress(newAddress.firstLine, newAddress.secondLine,
          newAddress.postCode, newAddress.locality, newAddress.province,
          newAddress.country)

      Then("Address must be saved in DB")
      Then("Address must be created")
      verify(addressService.addressDao).save(newAddress)
      createdAddress should equal(newAddress)

    }
  }

  feature("Address remove") {
    scenario("An Address must be removed if it exists") {

      Given("An existent Address")
      val addressId: Long = 1

      Mockito.when(addressService.addressDao.findById(addressId)).thenReturn(Some(validAddress))

      When("Address try to be removed")
      addressService.removeAddress(addressId)

      Then("Address must be removed")
      verify(addressService.addressDao).remove(validAddress)

    }

    scenario("An Address can't be removed if it doesn't exists") {

      Given("A not existent Address")
      val addressId: Long = 1

      Mockito.when(addressService.addressDao.findById(addressId)).thenReturn(None)

      When("Address try to be removed")
      addressService.removeAddress(addressId)

      Then("Address can't be removed")
      verify(addressService.addressDao, never).remove(validAddress)

    }
  }
}


