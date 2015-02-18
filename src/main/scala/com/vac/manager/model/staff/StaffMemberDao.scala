package com.vac.manager.model.staff

import com.vac.manager.model.generic.GenericDao

trait StaffMemberDao extends GenericDao[StaffMember, java.lang.Long] {

  def findByName(name: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findAllByFederationId(fedId: Long): Seq[StaffMember]

  def findAllByActivated(activated: Boolean, startIndex: Int, count: Int): Seq[StaffMember]

  def findByEmail(email: String, startIndex: Int, count: Int): Seq[StaffMember]

  def findByCardId(cardId: String, startIndex: Int, count: Int): Seq[StaffMember]

}