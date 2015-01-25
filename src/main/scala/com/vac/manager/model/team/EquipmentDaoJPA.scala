package com.vac.manager.model.team

import com.vac.manager.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("equipmentDao")
class EquipmentDaoJPA
		extends GenericDaoJPA[Equipment,java.lang.Long](classOf[Equipment])
		with EquipmentDao {

}