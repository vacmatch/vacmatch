package com.vac.manager.service.staff

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import com.vac.manager.model.staff.StaffMemberDao
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import scala.collection.JavaConverters._
import com.vac.manager.model.personal.Address
import java.util.Calendar
import org.springframework.transaction.annotation.Transactional
import scravatar.Gravatar
import com.vac.manager.model.federation.Federation
import com.vac.manager.service.federation.FederationService
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException
import com.vac.manager.model.generic.exceptions.IllegalArgumentException
import com.vac.manager.service.personal.AddressService

@Service("staffMemberService")
@Transactional
class StaffMemberServiceImpl extends StaffMemberService {

  @Autowired
  var federationService: FederationService = _

  @Autowired
  var addressService: AddressService = _

  @Autowired
  var staffMemberDao: StaffMemberDao = _

  /* --------------- FIND ---------------- */

  def find(staffId: Long): Option[StaffMember] = {
    staffMemberDao.findById(staffId)
  }

  def findAllByFederationId(fedId: Long): Seq[StaffMember] = {
    staffMemberDao.findAllByFederationId(fedId)
  }

  def findByName(name: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findByName(name, startIndex, count)
  }

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findAllByActivated(activated, startIndex, count)
  }

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findByEmail(email, startIndex, count)
  }

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[StaffMember] = {
    staffMemberDao.findByCardId(cardId, startIndex, count)
  }

  /* ---------------- MODIFY --------------- */

  def changeActivation(staffId: Long, newState: Boolean) = {
    var maybeStaff: Option[StaffMember] = staffMemberDao.findById(staffId)

    maybeStaff match {
      case None => throw new InstanceNotFoundException(staffId, classOf[StaffMember].getName())
      case Some(stStaff) => {
        stStaff.staffActivated = newState
        staffMemberDao.save(stStaff)
      }
    }
  }

  def changePrivacy(staffId: Long, newState: Boolean, newAlias: String) = {
    var staff: Option[StaffMember] = staffMemberDao.findById(staffId)

    staff.map(_.staffAlias = newAlias)
    staff.map(staffMemberDao.save(_))
  }

  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team]) = {
    var staff: Option[StaffMember] = staffMemberDao.findById(staffId)

    if (newTeamList != null) {
      staff.map { s =>
        s.staffTeamList = newTeamList.asJava
        staffMemberDao.save(s)
      }
    }
  }

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createStaff(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String,
    stCardId: String, stBirth: Calendar, idFederation: Long): StaffMember = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stCardId)

    var maybeFederation: Option[Federation] = federationService.find(idFederation)

    maybeFederation match {
      case None => throw new InstanceNotFoundException(idFederation, classOf[Federation].getName())
      case Some(stFederation) => {
        var staff: StaffMember = new StaffMember(stName, stSurnames, stEmail, stTelephones,
          stCardId, stBirth, stFederation)

        staffMemberDao.save(staff)
        staff
      }
    }
  }

  @throws[IllegalArgumentException]
  def modifyStaff(staffId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stCardId: String, stBirth: Calendar): Option[StaffMember] = {

    //Check if there's an incorrect parameter
    checkParameters(stName, stSurnames, stEmail, stTelephones, stBirth, stCardId)

    //Modify address
    var maybeStaff: Option[StaffMember] = assignAddress(staffId, stAddress)

    maybeStaff.map { staff =>
      staff.staffName = stName
      staff.staffSurnames = stSurnames
      staff.staffEmail = stEmail
      staff.staffAvatarLink = new Gravatar(if (stEmail == null) "" else stEmail).ssl(true).avatarUrl
      staff.staffTelephones = stTelephones
      staff.staffAddress = stAddress
      staff.staffCardId = stCardId
      staff.staffBirth = stBirth
      staffMemberDao.save(staff)
    }
    maybeStaff
  }

  def assignAddress(staffId: Long, stAddress: Address): Option[StaffMember] = {

    val maybeStaff: Option[StaffMember] = find(staffId)

    maybeStaff.map { staff =>
      if ((stAddress == null) || (staff.staffAddress == stAddress))
        return maybeStaff

      if (staff.staffAddress != null)
        addressService.removeAddress(staff.staffAddress.addressId)

      val savedAddress: Address = addressService.createAddress(
        stAddress.firstLine, stAddress.secondLine,
        stAddress.postCode, stAddress.locality,
        stAddress.province, stAddress.country)

      staff.staffAddress = savedAddress
      staffMemberDao.save(staff)
    }
    maybeStaff
  }

  @throws[IllegalArgumentException]
  protected def checkParameters(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stCardId: String) {

    val checkAgainstNull = List((stName, classOf[String]), (stSurnames, classOf[String]),
      (stCardId, classOf[String]), (stBirth, classOf[Calendar]))
    val checkAgainstEmpty = List((stName, classOf[String]), (stSurnames,
      classOf[String]), (stCardId, classOf[String]))

    checkAgainstNull.map {
      case (elt, cls) =>
        if (Option(elt).isEmpty)
          throw new IllegalArgumentException(elt, cls.getName())
    }
    checkAgainstEmpty.map {
      case (elt, cls) =>
        if (Option(elt).exists(_.trim == ""))
          throw new IllegalArgumentException(elt, cls.getName())
    }
  }

  def getSurnamesFromString(surnames: String): Seq[String] = {
    surnames.split(" ")
  }

  def getTelephonesFromString(telephones: String): Seq[String] = {
    telephones.split(", ")
  }

}

