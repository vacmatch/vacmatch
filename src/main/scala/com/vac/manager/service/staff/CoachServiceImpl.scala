package com.vac.manager.service.staff

import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.CoachDao
import com.vac.manager.model.personal.Address
import com.vac.manager.model.staff.License
import com.vac.manager.model.staff.Coach
import java.util.Calendar
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import org.springframework.transaction.annotation.Transactional

@Service("coachService")
@Transactional
class CoachServiceImpl 
				extends StaffServiceImpl
				with CoachService {

  @Autowired
  var coachDao: CoachDao = _

  
  /* --------------- FIND ---------------- */

  override
  def findByStaffId(staffId: Long, fedId: Long): Option[Coach] = {
    this.coachDao.findByStaffId(staffId, fedId)
  }
  
  override
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Coach] =  {
	this.coachDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  override
  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findAllByActivated(activated, startIndex, count)
  }
	
  override
  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findByEmail(email, startIndex, count)
  }
	
  override
  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findByNif(nif, startIndex, count)
  }

  
  /* ------------- MODIFY --------------- */
  
  @throws[InstanceNotFoundException]
  def createCoach(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, idFederation: Long, licen: License): Coach = {
    
    var maybeFederation: Option[Federation] = federationService.find(idFederation)

    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
        var coach: Coach = new Coach(stName, stSurnames, stEmail, 
          stTelephones, stAddress, stNif, stBirth, stFederation, licen)
	
	    coachDao.save(coach)
	    coach
      }
    }
  }
    
  def modifyCoach(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Option[Coach] = {
    
    var maybeCoach: Option[Coach] = coachDao.findByStaffId(staffId, fedId)
    
    maybeCoach match {
      case None => 
      case Some(coach) =>{
	    coach.staffName = stName
	    coach.staffSurnames = stSurnames.asJava
	    coach.staffEmail = stEmail
	    coach.staffTelephones = stTelephones.asJava
	    coach.staffAddress = stAddress
	    coach.staffNif = stNif
	    coach.staffBirth = stBirth
	    coach.coachLicense = licen
	    
	    coachDao.save(coach)
      }
    }
    maybeCoach
  }
 
  /* ------------- DELETE ---------------- */
  
  
}



