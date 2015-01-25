package com.vac.manager.service.staff

import com.vac.manager.model.staff.Player
import com.vac.manager.model.staff.PlayerStatistics
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.team.Team
import com.vac.manager.model.federation.Federation
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait PlayerService extends StaffService {

  @throws[InstanceNotFoundException]
  def createPlayer(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, num: Int): Player
    
  def modifyPlayer(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, num: Int): Option[Player]
  
  def modifyPlayerStatistics(staffId: Long, newStats: PlayerStatistics)
	
}


