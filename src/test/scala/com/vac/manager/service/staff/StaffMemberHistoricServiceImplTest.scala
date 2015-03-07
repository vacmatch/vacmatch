package com.vac.manager.service.staff

import org.scalatest.GivenWhenThen
import org.scalatest.PropSpec
import com.vac.manager.model.team.Team
import org.mockito.Mockito
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.staff.StaffMemberHistoric
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.mockito.Mockito._
import java.util.Calendar
import com.vac.manager.model.personal.Address
import scala.collection.JavaConverters._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import com.vac.manager.model.staff.StaffMemberHistoricDao

class StaffMemberHistoricServiceImplTest
  extends PropSpec
  with GivenWhenThen
  with MockitoSugar
  with BeforeAndAfter {

  var staffHistoricService: StaffMemberHistoricServiceImpl = _

  val validTeam = new Team(
    "El chiringuito FC",
    "El chiringuito Football Club",
    Calendar.getInstance(),
    new Address,
    "wwww.chiringuitofc.com",
    List("699555412").asJava)

  val validStaff = new StaffMember()
  val validStaffHistoric = new StaffMemberHistoric()

  before {
    staffHistoricService = new StaffMemberHistoricServiceImpl
    staffHistoricService.staffHistoricDao = mock[StaffMemberHistoricDao]
  }

  // Create staff member historic
  property("A staffHistoric can't be created if this staff was created previously with this team" +
    "and exists an activated staff historic in that team.") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existent staff")
    val staff: StaffMember = validStaff
    staff.staffId = 1

    Given("A historic between Staff and Team")
    val staffHistoric: StaffMemberHistoric = validStaffHistoric
    staffHistoric.staffHistoricId = 1
    Mockito.when(staffHistoricService.staffHistoricDao
      .findActivatedElement(staff.staffId, team.teamId)).thenReturn(Some(staffHistoric))

    When("Try to create a new staff historic")
    val eitherTeam: Either[Exception, StaffMemberHistoric] = staffHistoricService.create(staff, team)

    Then("Must return an error")
    assert(eitherTeam.isLeft)

    Then("Must return an duplicate instance exception")
    assert(eitherTeam.left.get.isInstanceOf[DuplicateInstanceException])

  }

}