package main.scala.service.staff

import main.scala.model.staff.Staff
import main.scala.model.team.Team
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.staff.Player
import main.scala.model.staff.PlayerStatistics
import main.scala.model.staff.License
import main.scala.model.staff.Coach

trait StaffService {

  /* --------------- FIND ---------------- */

  def findByStaffId(staffId: Long, fedId: Long): Option[Staff]

  def findAllByFederationId(fedId: Long): Seq[Staff]

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff]

  /* ---------------- MODIFY --------------- */

  def changeActivation(staffId: Long, newState: Boolean)

  def changePrivacity(staffId: Long, newState: Boolean, newAlias: String)

  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team])

  def createStaff(stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar): Staff

  def modifyStaff(staffId: Long, fedId: Long, stName: String, stSurnames: Seq[String],
    stEmail: String, stTelephones: Seq[String], stAddress: Address,
    stNif: String, stBirth: Calendar): Option[Staff]

  /* ---------------- DELETE ---------------- */

  /* ---------------- DELETE ---------------- */


}
