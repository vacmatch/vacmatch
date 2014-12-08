package main.scala.model.personal

import org.springframework.stereotype.Repository
import main.scala.model.generic.GenericDaoJPA

@Repository("avatarDao")
class AvatarDaoJPA 
		extends GenericDaoJPA[Avatar,java.lang.Long](classOf[Avatar]) 
		with AvatarDao {
}