package main.scala.service.team

import main.scala.model.team.Equipment

trait EquipmentService {
  
  def findByEquipmentId(eqId: Long): Equipment
  
  def findByEquipmentType(eqType: String): Seq[Equipment]
  
  def createEquipment(eqType: String, eqShirt: String, eqShorts: String,
      eqTights: String): Equipment
  
  def modifyEquipment(equipId: Long, eqType: String, eqShirt: String,
      eqShorts: String, eqTights: String): Equipment
  
  def deleteEquipment(equipId: Long): Equipment
  
  
}


