package main.scala.service.staff

import main.scala.model.staff.Player
import main.scala.model.staff.PlayerStatistics
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.team.Team
import main.scala.model.federation.Federation

trait PlayerService extends StaffService {

  def createPlayer(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, num: Int): Player
    
  def modifyPlayer(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, num: Int): Option[Player]
  
  def modifyPlayerStatistics(staffId: Long, newStats: PlayerStatistics)
	
}


