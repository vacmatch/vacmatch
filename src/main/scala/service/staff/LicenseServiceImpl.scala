package main.scala.service.staff

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("licenseService")
@Transactional
class LicenseServiceImpl extends LicenseService {

}