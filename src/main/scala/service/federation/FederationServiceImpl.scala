package main.scala.service.federation

import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.federation.FederationDao
import main.scala.model.federation.Federation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("federationService")
@Transactional
class FederationServiceImpl
				extends FederationService{
  
  @Autowired
  var federationDao: FederationDao = _

  def findById(fedId: Long): Option[Federation] = {
	Option(federationDao.findById(fedId))
  }
  
}