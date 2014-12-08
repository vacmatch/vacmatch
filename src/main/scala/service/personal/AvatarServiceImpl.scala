package main.scala.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import main.scala.model.personal.Avatar

@Service("avatarService")
@Transactional
class AvatarServiceImpl extends AvatarService {
  
  def findByAvatarId(avatarId: Long): Avatar = {
    null
  }
  
  def uploadAvatar(img: Array[Byte]): Avatar = {
    null
  }
  
  def deleteAvatar(avId: Long) = {
    
  }
  
}