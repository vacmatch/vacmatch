package com.vac.manager.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.vac.manager.model.team.Equipment

@Service("equipmentService")
@Transactional
class EquipmentServiceImpl extends EquipmentService {
  
  def findByEquipmentId(eqId: Long): Equipment = {
    null
  }
  
  def findByEquipmentType(eqType: String): Seq[Equipment] = {
    null
  }
  
  def createEquipment(eqType: String, eqShirt: String, eqShorts: String,
      eqTights: String): Equipment = {
    null
  }
  
  def modifyEquipment(equipId: Long, eqType: String, eqShirt: String,
      eqShorts: String, eqTights: String): Equipment = {
    null
  }
  
  def deleteEquipment(equipId: Long): Equipment = {
    null
  }
  
}


