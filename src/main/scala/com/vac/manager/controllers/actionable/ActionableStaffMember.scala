package com.vac.manager.controllers.actionable

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.controllers.utils.UrlGrabber

class ActionableStaffMember(staffMember: StaffMember)
    extends StaffMember
    with UrlGrabber {

  staffMemberId = staffMember.staffMemberId
  joinDate = staffMember.joinDate
  exitDate = staffMember.exitDate
  person = staffMember.person
  staffTeam = staffMember.staffTeam

  def getUnAssignPostLink(): String = {
    getUrl("TeamAdminController.unAssignStaffMemberPost", "personId" -> person.personId)
  }

}