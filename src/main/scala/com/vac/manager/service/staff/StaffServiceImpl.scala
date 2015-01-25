package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.StaffDao
import com.vac.manager.model.staff.Staff
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import com.vac.manager.model.staff.PlayerDao
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.Player
import java.util.Calendar
import com.vac.manager.model.staff.PlayerStatistics
import com.vac.manager.model.staff.License
import com.vac.manager.model.staff.Coach
import com.vac.manager.model.staff.CoachDao
import org.springframework.transaction.annotation.Transactional
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

@Service("staffService")
@Transactional
class StaffServiceImpl extends StaffService {
  
  @Autowired
  var federationService: FederationService = _
  
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

  @throws[InstanceNotFoundException]
  def createStaff(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, idFederation: Long): Staff = {
    
    var maybeFederation: Option[Federation] = federationService.find(idFederation)
    
    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
	    var staff: Staff = new Staff(stName, stSurnames, stEmail, stTelephones,
	        stAddress, stNif, stBirth, stFederation)
	  	
	    staffDao.save(staff)
	    staff
      }
    }
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

  def getSurnamesFromString(surnames: String): Seq[String] = {
    surnames.split(" ")
  }
  
  def getTelephonesFromString(telephones: String): Seq[String] = {
    telephones.split(", ")
  }
  
}


