package main.scala.service.federation

import main.scala.model.generic.GenericDao
import main.scala.model.federation.Federation

trait FederationService {

  def findById(fedId: Long): Option[Federation]

}

