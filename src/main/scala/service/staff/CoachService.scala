package main.scala.service.staff

import main.scala.model.personal.Address
import main.scala.model.staff.Coach
import java.util.Calendar
import main.scala.model.staff.License
import main.scala.model.federation.Federation
import main.scala.model.generic.exceptions.InstanceNotFoundException

trait CoachService extends StaffService {
  
  @throws[InstanceNotFoundException]
  def createCoach(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, licen: License): Coach
    
  def modifyCoach(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Option[Coach]
  

}


