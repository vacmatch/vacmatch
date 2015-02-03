package com.vac.manager.service.staff

import com.vac.manager.model.staff.Staff
import com.vac.manager.model.team.Team
import com.vac.manager.model.personal.Address
import java.util.Calendar
import com.vac.manager.model.staff.Player
import com.vac.manager.model.staff.PlayerStatistics
import com.vac.manager.model.staff.License
import com.vac.manager.model.staff.Coach
import com.vac.manager.model.generic.exceptions.InstanceNotFoundException

trait StaffService {

  /* --------------- FIND ---------------- */

  def find(staffId: Long): Option[Staff]

  def findAllByFederationId(fedId: Long): Seq[Staff]

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff]

  /* ---------------- MODIFY --------------- */

  @throws[InstanceNotFoundException]
  def changeActivation(staffId: Long, newState: Boolean)

  def changePrivacity(staffId: Long, newState: Boolean, newAlias: String)

  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team])

  @throws[InstanceNotFoundException]
  @throws[IllegalArgumentException]
  def createStaff(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar, idFederation: Long): Staff

  def modifyStaff(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar): Option[Staff]

  def getSurnamesFromString(surnames: String): Seq[String]

  def getTelephonesFromString(telephones: String): Seq[String]

  /* ---------------- DELETE ---------------- */

  /* ---------------- DELETE ---------------- */


}
