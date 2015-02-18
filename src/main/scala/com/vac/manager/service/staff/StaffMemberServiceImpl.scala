package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.StaffMemberDao
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import com.vac.manager.model.staff.PlayerDao
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.Player
import java.util.Calendar
import com.vac.manager.model.staff.Coach
import com.vac.manager.model.staff.CoachDao
import org.springframework.transaction.annotation.Transactional
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException

@Service("staffMemberService")
@Transactional
class StaffMemberServiceImpl extends StaffMemberService {
  
  @Autowired
  var federationService: FederationService = _
  
  @Autowired
  var staffMemberDao: StaffMemberDao = _
  
  /* --------------- FIND ---------------- */

  def find(staffId: Long): Option[StaffMember] = {
    Option(staffMemberDao.findById(staffId))
  }
  
  def findAllByFederationId(fedId: Long): Seq[StaffMember] = {
    staffMemberDao.findAllByFederationId(fedId)
  }

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[StaffMember] =  {
	staffMemberDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findAllByActivated(activated, startIndex, count)
  }
	
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findByEmail(email, startIndex, count)
  }
	
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findByNif(nif, startIndex, count)
  }

	
  /* ---------------- MODIFY --------------- */
	
  @throws[InstanceNotFoundException]
  def changeActivation(staffId: Long, newState: Boolean) = {
    var maybeStaff: Option[StaffMember] = Option(staffMemberDao.findById(staffId))
    
    maybeStaff match {
      case None => throw new InstanceNotFoundException(staffId, classOf[StaffMember].getName())
      case Some(stStaff) => {
        stStaff.staffActivated = newState
        staffMemberDao.save(stStaff)
      }
    }
  }
	
  def changePrivacy(staffId: Long, newState: Boolean, newAlias: String) = {
    var staff: StaffMember = staffMemberDao.findById(staffId)
    
    if(newAlias != null){
    	staff.staffPrivacyActivated = newState
    	staff.staffAlias = newAlias
    	staffMemberDao.save(staff)
    }
  }
	
  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team]) = {
    var staff: StaffMember = staffMemberDao.findById(staffId)
    
    if(newTeamList != null){
    	staff.staffTeamList = newTeamList.asJava
    	staffMemberDao.save(staff)
    }
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createStaff(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar, idFederation: Long): StaffMember = {
    
    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stNif)
    
    var maybeFederation: Option[Federation] = federationService.find(idFederation)
    
    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
	    var staff: StaffMember = new StaffMember(stName, stSurnames, stEmail, stTelephones,
	        stAddress, stNif, stBirth, stFederation)
	  	
	    staffMemberDao.save(staff)
	    staff
      }
    }
  }

  @throws[IllegalArgumentException]
  def modifyStaff(staffId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar): Option[StaffMember] = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stNif)

    var maybeStaff: Option[StaffMember] = find(staffId)
    
    maybeStaff match {
      case None =>
      case Some(staff) => {
  	    staff.staffName = stName
        staff.staffSurnames = stSurnames
	    staff.staffEmail = stEmail
	    staff.staffAvatarLink = new Gravatar(if(stEmail==null) "" else stEmail).ssl(true).avatarUrl
	    staff.staffTelephones = stTelephones
	    staff.staffAddress = stAddress
	    staff.staffNif = stNif
	    staff.staffBirth = stBirth
	  staffMemberDao.save(staff)
	  }
    }
    maybeStaff
  }

  @throws[IllegalArgumentException]
  protected def checkParameters(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stNif: String) {
    
    if ((stName == null) || (stName.isEmpty()))
      throw new IllegalArgumentException(stName, classOf[String].getName())
    if ((stSurnames == null) || (stSurnames.isEmpty))
      throw new IllegalArgumentException(stSurnames, classOf[Seq[String]].getName())
    if((stNif == null) || (stNif.isEmpty()))
      throw new IllegalArgumentException(stNif, classOf[String].getName())
    if((stBirth == null))
      throw new IllegalArgumentException(stBirth, classOf[Calendar].getName())
  }

  def getSurnamesFromString(surnames: String): Seq[String] = {
    surnames.split(" ")
  }
  
  def getTelephonesFromString(telephones: String): Seq[String] = {
    telephones.split(", ")
  }
  
}


