package com.vac.manager.service.staff

import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.Coach
import java.util.Calendar
import com.vac.manager.model.federation.Federation
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait CoachService extends StaffService {
  
  @throws[InstanceNotFoundException]
  def createCoach(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long): Coach
    
  @throws[InstanceNotFoundException]
  def modifyCoach(staffId: Long, fedId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar): Option[Coach]
  

}


