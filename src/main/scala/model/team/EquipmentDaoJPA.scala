package main.scala.model.team

import main.scala.model.generic.GenericDaoJPA

class EquipmentDaoJPA
		extends GenericDaoJPA[Equipment,java.lang.Long](classOf[Equipment])
		with EquipmentDao {

}