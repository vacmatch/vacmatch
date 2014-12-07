package main.scala.service.personal

import main.scala.model.personal.Avatar

trait AvatarService {

  def findByAvatarId(avatarId: Long): Avatar
  
  def uploadAvatar(img: Array[Byte]): Avatar
  
  def deleteAvatar(avId: Long)
  
  
}