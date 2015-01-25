package com.vac.manager.service.staff

import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.Coach
import java.util.Calendar
import com.vac.manager.model.staff.License
import com.vac.manager.model.federation.Federation
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait CoachService extends StaffService {
  
  @throws[InstanceNotFoundException]
  def createCoach(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, licen: License): Coach
    
  def modifyCoach(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Option[Coach]
  

}


