package main.scala.model.team

import main.scala.model.generic.GenericDaoJPA
import org.springframework.stereotype.Repository

@Repository("equipmentDao")
class EquipmentDaoJPA
		extends GenericDaoJPA[Equipment,java.lang.Long](classOf[Equipment])
		with EquipmentDao {

}