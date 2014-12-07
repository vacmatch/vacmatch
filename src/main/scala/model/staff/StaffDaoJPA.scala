package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("staffDao")
class StaffDaoJPA 
		extends GenericDaoJPA[Staff,java.lang.Long](classOf[Staff])
		with StaffDao {
  
}