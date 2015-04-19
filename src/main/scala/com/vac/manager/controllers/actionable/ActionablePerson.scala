package com.vac.manager.controllers.actionable

import com.vac.manager.model.staff.Person
import com.vac.manager.controllers.utils.UrlGrabber
import javax.persistence.Entity
import javax.persistence.Inheritance
import javax.persistence.Table
import com.vac.manager.controllers.utils.Hyperlink
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class ActionablePerson(person: Person, userCanEdit: Boolean)
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

  @BeanProperty
  val anonymousLinks = List(Hyperlink("Show person", getShowLink, "btn-primary")).asJava

  @BeanProperty
  val authorizedLinks = if (!userCanEdit) List().asJava else
    List(Hyperlink("Edit person", getEditLink, "btn-default")).asJava

  @BeanProperty
  val links = (anonymousLinks.asScala ++ authorizedLinks.asScala).asJava

  def getShowLink(): String = {
    getUrl("PersonAdminController.showPerson", "personId" -> person.personId)
  }

  def getEditLink(): String = {
    getUrl("PersonAdminController.edit", "personId" -> person.personId)
  }

  def getAssignPostLink(): String = {
    getUrl("TeamAdminController.assignStaffMemberPost", "personId" -> personId)
  }

  def getAssignTeamLink: String = ""
  def getEditPrivacyLink: String = ""
}

