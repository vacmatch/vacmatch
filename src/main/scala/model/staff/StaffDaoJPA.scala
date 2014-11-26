package main.scala.model.staff

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoHibernate

@Repository("staffDao")
class StaffDaoJPA 
		extends GenericDaoHibernate[Staff,java.lang.Long](classOf[Staff])
		with StaffDao {

}