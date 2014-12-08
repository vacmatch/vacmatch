package main.scala.service.staff

import main.scala.model.staff.Staff
import main.scala.model.personal.Avatar
import main.scala.model.team.Team
import main.scala.model.personal.Address
import java.util.Calendar
import main.scala.model.staff.Player
import main.scala.model.staff.PlayerStatistics
import main.scala.model.staff.License
import main.scala.model.staff.Coach

trait StaffService {

  /* --------------- FIND ---------------- */

  def findByStaffId(staffId: Long): Staff

  def findByNameAndSurname(name: String, surname: String, startIndex: Int, count: Int): Seq[Staff]

  def findAll(startIndex: Int, count: Int): Seq[Staff]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[Staff]

  def findByAlias(alias: String, startIndex: Int, count: Int): Seq[Staff]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[Staff]

  def findByNif(nif: String, startIndex: Int, count: Int): Seq[Staff]

  def findAvatarHistoricByStaffId(staffId: Long): Seq[Avatar]
  
  /* ---------------- MODIFY --------------- */

  def changeActivation(staffId: Long, newState: Boolean)

  def changePrivacity(staffId: Long, newState: Boolean, newAlias: String)

  def assignAvatar(staffId: Long, newAvatar: Avatar)

  def addTeamToStaff(staffId: Long, newTeamList: Seq[Team])

  /*
         * createStaff( "parameters" ): Staff
         *
         * modifyStaff(staffId: Long, "parameters" ): Staff
         */

  /* ---------------- DELETE ---------------- */


}
