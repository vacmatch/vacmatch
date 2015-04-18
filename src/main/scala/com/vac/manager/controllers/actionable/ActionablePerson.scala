package com.vac.manager.controllers.actionable

import com.vac.manager.model.staff.Person
import com.vac.manager.controllers.utils.UrlGrabber
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.Table

class ActionablePerson(person: Person)
    extends Person()
    with UrlGrabber {

  personId = person.personId
  name = person.name
  surnames = person.surnames
  alias = person.alias
  email = person.email
  avatarLink = person.avatarLink
  telephones = person.telephones
  address = person.address
  cardId = person.cardId
  birth = person.birth
  federation = person.federation

  def getShowLink(): String = {
    getUrl("PersonController.showPerson", "personId" -> person.personId)
  }

  def getEditLink(): String = {
    getUrl("PersonController.edit", "personId" -> person.personId)
  }

  def getAssignPostLink(): String = {
    getUrl("TeamAdminController.assignStaffMemberPost", "personId" -> personId)
  }

  def getAssignTeamLink: String = ""
  def getEditPrivacyLink: String = ""
}

