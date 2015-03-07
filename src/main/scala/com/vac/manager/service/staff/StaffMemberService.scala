package com.vac.manager.service.staff

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait StaffMemberService {

  def find(staffId: Long): Option[StaffMember]

  def findAllByFederationId(fedId: Long): Seq[StaffMember]

  def findByName(name: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[StaffMember]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[StaffMember]

  @throws[InstanceNotFoundException]
  def changeActivation(staffId: Long, newState: Boolean)

  def changePrivacy(staffId: Long, newState: Boolean, newAlias: String)

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createStaff(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stCardId: String,
    stBirth: Calendar, idFederation: Long): StaffMember

  @throws[IllegalArgumentException]
  def modifyStaff(staffId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stCardId: String, stBirth: Calendar): Option[StaffMember]

  def assignAddress(staffId: Long, stAddress: Address): Option[StaffMember]
  
  protected def checkParameters(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stBirth: Calendar, stCardId: String)
  
}
