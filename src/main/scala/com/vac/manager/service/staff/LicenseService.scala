package com.vac.manager.service.staff

import com.vac.manager.model.staff.License

trait LicenseService {
  
  def findByLicenseId(licenID: Long): License
  
  def findByLicenseType(liType: Long): License
  
  def createLicense(liType: String, liDef: String): License
  
  def modifyLicense(licId: Long, liType: String, liDef: String): License
  
  def deleteLicense(licId: Long)
  
}


