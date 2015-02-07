package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.staff.Player
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.StaffDao
import com.vac.manager.model.staff.PlayerDao
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.staff.Player
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

@Service("playerService")
@Transactional
class PlayerServiceImpl
				extends StaffServiceImpl
				with PlayerService {
  
  @Autowired
  var playerDao: PlayerDao = _

  /* --------------- FIND ---------------- */

  override
  def find(staffId: Long): Option[Player] = {
    Option((playerDao.findById(staffId)).asInstanceOf[Player])
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
  def createPlayer(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar,  idFederation: Long, num: String): Player = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stNif)

    var maybeFederation: Option[Federation] = federationService.find(idFederation)

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
  
  @throws[InstanceNotFoundException]
  def modifyPlayer(staffId: Long, fedId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar, num: String): Option[Player] = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stNif)

    var maybePlayer: Option[Player] = Option(playerDao.findById(staffId))
    
    maybePlayer match {
      case None =>
      case Some(player) => {
	    player.staffName = stName
	    player.staffSurnames = stSurnames
	    player.staffEmail = stEmail
	    player.staffTelephones = stTelephones
	    player.staffAddress = stAddress
	    player.staffNif = stNif
	    player.staffBirth = stBirth
	    player.dorsal = num
	    
	    playerDao.save(player)
	  }
    }
    maybePlayer
  }
  
  /* ------------- DELETE ---------------- */
  
  
  
}



