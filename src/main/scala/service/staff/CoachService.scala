package main.scala.service.staff

import main.scala.model.personal.Address
import main.scala.model.staff.Coach
import java.util.Calendar
import main.scala.model.staff.License

trait CoachService extends StaffService {
  
  def createCoach(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Coach
    
  def modifyCoach(staffId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Coach
  

}


