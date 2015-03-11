package com.vac.manager.service.staff

import org.scalatest.GivenWhenThen
import org.scalatest.PropSpec
import com.vac.manager.model.team.Team
import org.mockito.Mockito
import com.vac.manager.model.staff.Person
import com.vac.manager.model.staff.StaffMember
import com.vac.manager.model.generic.exceptions.DuplicateInstanceException
import org.mockito.Mockito._
import java.util.Calendar
import com.vac.manager.model.personal.Address
import scala.collection.JavaConverters._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import com.vac.manager.model.staff.StaffMemberDao

class StaffMemberServiceImplTest
  extends PropSpec
  with GivenWhenThen
  with MockitoSugar
  with BeforeAndAfter {

  var staffMemberService: StaffMemberServiceImpl = _

  val validTeam = new Team(
    "El chiringuito FC",
    "El chiringuito Football Club",
    Calendar.getInstance(),
    new Address,
    "wwww.chiringuitofc.com",
    List("699555412").asJava)

  val validPerson = new Person()
  val validStaffMember = new StaffMember()

  before {
    staffMemberService = new StaffMemberServiceImpl
    staffMemberService.staffMemberDao = mock[StaffMemberDao]
  }

  // Create staff member historic
  property("A StaffMember can't be created if this person was created previously with this team" +
    "and exists an activated StaffMember in that team.") {

    Given("An existent team")
    val team: Team = validTeam
    team.teamId = 1

    Given("An existent person")
    val person: Person = validPerson
    person.personId = 1

    Given("A StaffMember between Person and Team")
    val staffMember: StaffMember = validStaffMember
    staffMember.staffMemberId = 1
    Mockito.when(staffMemberService.staffMemberDao
      .findActivatedElement(person.personId, team.teamId)).thenReturn(Some(staffMember))

    When("Try to create a new StaffMember")
    val eitherTeam: Either[Exception, StaffMember] = staffMemberService.create(person, team)

    Then("Must return an error")
    assert(eitherTeam.isLeft)

    Then("Must return an duplicate instance exception")
    assert(eitherTeam.left.get.isInstanceOf[DuplicateInstanceException])

  }

}