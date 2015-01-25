package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.staff.License

@Service("licenseService")
@Transactional
class LicenseServiceImpl extends LicenseService {
  
  def findByLicenseId(licenID: Long): License = {
    null
  }
  
  def findByLicenseType(liType: Long): License = {
    null
  }

  def createLicense(liType: String, liDef: String): License = {
    null
  }
  
  def modifyLicense(licId: Long, liType: String, liDef: String): License = {
    null
  }
  
  def deleteLicense(licId: Long) = {

  }
  
}