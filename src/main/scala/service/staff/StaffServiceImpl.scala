package main.scala.service.staff

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.staff.StaffDao
import main.scala.model.staff.Staff
import main.scala.model.team.Team
import scala.collection.JavaConverters._
import main.scala.model.staff.PlayerDao
import main.scala.model.personal.Address
import main.scala.model.staff.Player
import java.util.Calendar
import main.scala.model.staff.PlayerStatistics
import main.scala.model.staff.License
import main.scala.model.staff.Coach
import main.scala.model.staff.CoachDao
import org.springframework.transaction.annotation.Transactional
import scravatar.Gravatar

@Service("staffService")
@Transactional
class StaffServiceImpl extends StaffService {
  
  @Autowired
  var staffDao: StaffDao = _

  /* --------------- FIND ---------------- */

  def findByStaffId(staffId: Long, fedId: Long): Option[Staff] = {
	staffDao.findByStaffId(staffId, fedId)
  }
  
  def findAllByFederationId(fedId: Long): Seq[Staff] = {
    staffDao.findAllByFederationId(fedId)
  }

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff] =  {
	staffDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff] = {
    staffDao.findAllByActivated(activated, startIndex, count)
  }
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff] = {
    staffDao.findByEmail(email, startIndex, count)
  }
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff] = {
    staffDao.findByNif(nif, startIndex, count)
  }

	
  /* ---------------- MODIFY --------------- */
	
  def changeActivation(staffId: Long, newState: Boolean) = {
    var staff: Staff = staffDao.findById(staffId)
    
	staff.staffActivated = newState
	staffDao.save(staff)
  }
	
  def changePrivacity(staffId: Long, newState: Boolean, newAlias: String) = {
    var staff: Staff = staffDao.findById(staffId)
    
    if(newAlias != null){
    	staff.staffPrivacityActivated = newState
    	staff.staffAlias = newAlias
    	staffDao.save(staff)
    }
  }
	
  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team]) = {
    var staff: Staff = staffDao.findById(staffId)
    
    if(newTeamList != null){
    	staff.staffTeamList = newTeamList.asJava
    	staffDao.save(staff)
    }
  }

  def createStaff(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar): Staff = {
    var staff: Staff = new Staff(stName, stSurnames, stEmail, stTelephones,
        stAddress, stNif, stBirth)
  	
    staffDao.save(staff)
    staff
  }
    
  def modifyStaff(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar): Option[Staff] = {
    
    var maybeStaff: Option[Staff] = staffDao.findByStaffId(staffId, fedId)

    maybeStaff match {
      case None =>
      case Some(staff) => {
  	    staff.staffName = stName
        staff.staffSurnames = stSurnames.asJava
	    staff.staffEmail = stEmail
	    staff.staffAvatarLink = new Gravatar(if(stEmail==null) "" else stEmail).ssl(true).avatarUrl
	    staff.staffTelephones = stTelephones.asJava
	    staff.staffAddress = stAddress
	    staff.staffNif = stNif
	    staff.staffBirth = stBirth
	  staffDao.save(staff)
	  }
    }
    maybeStaff
  }
  
}


