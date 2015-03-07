package com.vac.manager.controllers.actionable

import com.vac.manager.model.staff.StaffMember
import com.vac.manager.controllers.utils.UrlGrabber
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.Table

class ActionableStaff(staff: StaffMember)
  extends StaffMember()
  with UrlGrabber {

  staffId = staff.staffId
  staffName = staff.staffName
  staffSurnames = staff.staffSurnames
  staffActivated = staff.staffActivated
  staffAlias = staff.staffAlias
  staffEmail = staff.staffEmail
  staffAvatarLink = staff.staffAvatarLink
  staffTelephones = staff.staffTelephones
  staffAddress = staff.staffAddress
  staffCardId = staff.staffCardId
  staffBirth = staff.staffBirth
  staffHistoricList = staff.staffHistoricList
  staffFederation = staff.staffFederation

  def getShowLink(): String = {
    getUrl("StaffController.showStaff", "staffId" -> staff.staffId)
  }

  def getEditLink(): String = {
    getUrl("StaffController.edit", "staffId" -> staff.staffId)
  }

  def getAssignPostLink(): String = {
    getUrl("TeamController.assignStaffPost", "staffId" -> staffId)
  }

  def getRemoveLink: String = ""
  def getAssignTeamLink: String = ""
  def getEditPrivacyLink: String = ""
}

