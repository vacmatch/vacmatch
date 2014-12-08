package main.scala.service.staff

import org.springframework.beans.factory.annotation.Autowired
import main.scala.model.staff.CoachDao
import main.scala.model.personal.Address
import main.scala.model.staff.License
import main.scala.model.staff.Coach
import java.util.Calendar
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
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
  def findByStaffId(staffId: Long): Coach = {
    this.coachDao.findById(staffId)
  }
  
  override
  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Coach] =  {
	this.coachDao.findByNameAndSurname(name, surname, startIndex, count)
  }

  override
  def findAll(startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findAll(startIndex, count)
  }

  override
  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findAllByActivated(activated, startIndex, count)
  }
	
  override
  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Coach] = {
    this.coachDao.findByAlias(alias, startIndex, count)
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
  
  def createCoach(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Coach = {
    
    var coach: Coach = new Coach(stName, stSurnames, stEmail, 
        stTelephones, stAddress, stNif, stBirth, licen)

    coachDao.save(coach)
    coach
  }
    
  def modifyCoach(staffId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, licen: License): Coach = {
    
    var coach: Coach = coachDao.findById(staffId)
    
    coach.staffName = stName
    coach.staffSurnames = stSurnames.asJava
    coach.staffEmail = stEmail
    coach.staffTelephones = stTelephones.asJava
    coach.staffAddress = stAddress
    coach.staffNif = stNif
    coach.staffBirth = stBirth
    coach.coachLicense = licen
    
    coachDao.save(coach)
    coach
  }
 
  /* ------------- DELETE ---------------- */
  
  
}



