package main.scala.service.staff

import org.springframework.stereotype.Service
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.staff.Player
import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.staff.StaffDao
import main.scala.model.staff.PlayerDao
import javax.transaction.Transactional
import main.scala.model.staff.PlayerStatistics
import main.scala.model.staff.Staff
import main.scala.model.personal.Avatar
import main.scala.model.team.Team
import scala.collection.JavaConverters._

@Service("playerService")
@Transactional
class PlayerServiceJPA extends PlayerService {
  
  @Autowired
  var playerDao: PlayerDao = _
    

  /* --------------- FIND ---------------- */

  def findByStaffId(staffId: Long): Player = {
    this.playerDao.findById(staffId)
  }
  
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Player] =  {
	this.playerDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  def findAll(startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findAll(startIndex, count)
  }

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findAllByActivated(activated, startIndex, count)
  }
	
  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findByAlias(alias, startIndex, count)
  }
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findByEmail(email, startIndex, count)
  }
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Player] = {
    this.playerDao.findByNif(nif, startIndex, count)
  }

  /* ------------- MODIFY --------------- */
  
  def createPlayer(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, num: Int): Player = {
    
    var player: Player = new Player(stName, stSurnames, stEmail, 
        stTelephones, stAddress, stNif, stBirth, num)

    playerDao.save(player)
    player
  }
  
  def modifyPlayer(staffId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, num: Int): Player = {
    
    var player: Player = playerDao.findById(staffId)
    
    player.staffName = stName
    player.staffSurnames = stSurnames.asJava
    player.staffEmail = stEmail
    player.staffTelephones = stTelephones.asJava
    player.staffAddress = stAddress
    player.staffNif = stNif
    player.staffBirth = stBirth
    player.playerNumber = num
    
    playerDao.save(player)
    player
  }
  
  def modifyPlayerStatistics(staffId: Long, newStats: PlayerStatistics) = {
	var player: Player = this.playerDao.findById(staffId).asInstanceOf[Player]
	
	player.playerStatistics = newStats
  }

  /* ------------- DELETE ---------------- */
  
  
  
}



