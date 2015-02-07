package com.vac.manager.service.staff

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.staff.Coach
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait StaffMemberService {

  /* --------------- FIND ---------------- */

  def find(staffId: Long): Option[StaffMember]

  def findAllByFederationId(fedId: Long): Seq[StaffMember]

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[StaffMember]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[StaffMember]

  /* ---------------- MODIFY --------------- */

  @throws[InstanceNotFoundException]
  def changeActivation(staffId: Long, newState: Boolean)

  def changePrivacity(staffId: Long, newState: Boolean, newAlias: String)

  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team])

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createStaff(stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar, idFederation: Long): StaffMember

  def modifyStaff(staffId: Long, fedId: Long, stName: String, stSurnames: String,
    stEmail: String, stTelephones: String, stAddress: Address,
    stNif: String, stBirth: Calendar): Option[StaffMember]

  def getSurnamesFromString(surnames: String): Seq[String]

  def getTelephonesFromString(telephones: String): Seq[String]

  /* ---------------- DELETE ---------------- */

  /* ---------------- DELETE ---------------- */


}
