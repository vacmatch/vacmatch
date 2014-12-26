package main.scala.service.staff

import org.springframework.stereotype.Service
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.staff.Player
import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.staff.StaffDao
import main.scala.model.staff.PlayerDao
import main.scala.model.staff.PlayerStatistics
import main.scala.model.staff.Staff
import main.scala.model.team.Team
import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional
import main.scala.model.staff.Player
import main.scala.model.federation.Federation
import main.scala.service.federation.FederationService
import main.scala.model.generic.exceptions.InstanceNotFoundException

@Service("playerService")
@Transactional
class PlayerServiceImpl
				extends StaffServiceImpl
				with PlayerService {
  
  @Autowired
  var playerDao: PlayerDao = _

  /* --------------- FIND ---------------- */

  override
  def findByStaffId(staffId: Long, fedId: Long): Option[Player] = {
    this.playerDao.findByStaffId(staffId, fedId)
  }
  
  override
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Player] =  {
	this.playerDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  override
  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findAllByActivated(activated, startIndex, count)
  }
	
  override
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findByEmail(email, startIndex, count)
  }
	
  override
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findByNif(nif, startIndex, count)
  }

  /* ------------- MODIFY --------------- */
  
  @throws[InstanceNotFoundException]
  def createPlayer(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, num: Int): Player = {

    var maybeFederation: Option[Federation] = federationService.findById(idFederation)

    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
	    var player: Player = new Player(stName, stSurnames, stEmail, 
	        stTelephones, stAddress, stNif, stBirth, stFederation, num)
	
	    playerDao.save(player)
	    player
      }
    }
  }
  
  def modifyPlayer(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, num: Int): Option[Player] = {
    
    var maybePlayer: Option[Player] = playerDao.findByStaffId(staffId, fedId)
    
    maybePlayer match {
      case None =>
      case Some(player) => {
	    player.staffName = stName
	    player.staffSurnames = stSurnames.asJava
	    player.staffEmail = stEmail
	    player.staffTelephones = stTelephones.asJava
	    player.staffAddress = stAddress
	    player.staffNif = stNif
	    player.staffBirth = stBirth
	    player.playerNumber = num
	    
	    playerDao.save(player)
	  }
    }
    maybePlayer
  }
  
  def modifyPlayerStatistics(staffId: Long, newStats: PlayerStatistics) = {
	var player: Player = this.playerDao.findById(staffId).asInstanceOf[Player]
	
	player.playerStatistics = newStats
  }

  /* ------------- DELETE ---------------- */
  
  
  
}



