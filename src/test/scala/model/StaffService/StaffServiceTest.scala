package test.scala.model.StaffService

import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.Test
import org.junit.Assert._
import main.scala.model.personal.Avatar
import org.springframework.beans.factory.annotation.Autowired
import main.scala.service.staff.StaffService
import main.scala.service.staff.PlayerService
import main.scala.service.staff.CoachService
import scala.collection.mutable.ArrayBuffer
import main.scala.service.personal.AddressService
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.staff.Staff
import main.scala.service.personal.AvatarService

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath:/application.xml", "classpath:/spring-config-test.xml"))
@Transactional
class StaffServiceTest {

  @Autowired
  var avatarService: AvatarService = _
  
  @Autowired
  var staffService: StaffService = _
  
  @Autowired
  var playerService: PlayerService = _
  
  @Autowired
  var coachService: CoachService = _
  
  @Autowired
  var addressService: AddressService = _
  
  
    @Test
  def findAvatarHistoricByStaffIdWithEmptyListtest() = {
    
    //Staff data
    var stName: String = "José"
    var stSurnames: Seq[String] = new ArrayBuffer[String] :+ "Castro" :+ "López"
    var stEmail: String = "test@email.com"
    var stTelephones: Seq[String] = new ArrayBuffer[String] :+ "123456789"
    var stAddress: Address = 	addressService.createAddress("USA")
    var stNif: String = "33333333Z"
    var stBirth: Calendar = Calendar.getInstance()
    var num: Int = 1
    
    //Create a player and get id
	var staff: Staff = playerService.createPlayer(stName, stSurnames, stEmail,
	    stTelephones, stAddress, stNif, stBirth, num)
    var staffId = staff.staffId 

    var expected: Seq[Avatar] = new ArrayBuffer[Avatar]
	var result: Seq[Avatar] = staffService.findAvatarHistoricByStaffId(staffId)
    
    assertEquals(result, expected)
    
  }
  
  @Test
  def findAvatarHistoricByStaffIdWithOneAvatartest() = {
    
    //Staff data
    var stName: String = "José"
    var stSurnames: Seq[String] = new ArrayBuffer[String] :+ "Castro" :+ "López"
    var stEmail: String = "test@email.com"
    var stTelephones: Seq[String] = new ArrayBuffer[String] :+ "123456789"
    var stAddress: Address = 	addressService.createAddress("USA")
    var stNif: String = "33333333Z"
    var stBirth: Calendar = Calendar.getInstance()
    var num: Int = 1
    
    //Create a player and get id
	var staff: Staff = playerService.createPlayer(stName, stSurnames, stEmail,
	    stTelephones, stAddress, stNif, stBirth, num)
    var staffId = staff.staffId 

    //Avatar data
    var img: Array[Byte] = null

    //Insert avatar and assign it to created staff
    var avatar: Avatar = avatarService.uploadAvatar(img)
	staffService.assignAvatar(staffId,avatar)
	
    var expected: Seq[Avatar] = new ArrayBuffer[Avatar] :+ avatar
	var result: Seq[Avatar] = staffService.findAvatarHistoricByStaffId(staffId)
    
    assertEquals(result, expected)
    
  }
  
  @Test
  def findAvatarHistoricByStaffIdWithSomeAvatarstest() = {
    
    //Staff data
    var stName: String = "José"
    var stSurnames: Seq[String] = new ArrayBuffer[String] :+ "Castro" :+ "López"
    var stEmail: String = "test@email.com"
    var stTelephones: Seq[String] = new ArrayBuffer[String] :+ "123456789"
    var stAddress: Address = 	addressService.createAddress("USA")
    var stNif: String = "33333333Z"
    var stBirth: Calendar = Calendar.getInstance()
    var num: Int = 1
    
    //Create a player and get id
	var staff: Staff = playerService.createPlayer(stName, stSurnames, stEmail,
	    stTelephones, stAddress, stNif, stBirth, num)
    var staffId = staff.staffId 

    //Avatar data
    var img: Array[Byte] = null

    //Insert avatars and assign them to created staff
    var avatar1: Avatar = avatarService.uploadAvatar(img)
    var avatar2: Avatar = avatarService.uploadAvatar(img)
    var avatar3: Avatar = avatarService.uploadAvatar(img)
	staffService.assignAvatar(staffId,avatar1)
	staffService.assignAvatar(staffId,avatar2)
	staffService.assignAvatar(staffId,avatar3)
	
    var expected: Seq[Avatar] = new ArrayBuffer[Avatar]:+avatar1:+avatar2:+avatar3
	var result: Seq[Avatar] = staffService.findAvatarHistoricByStaffId(staffId)
    
    assertEquals(result, expected)
    
  }  
  
}


