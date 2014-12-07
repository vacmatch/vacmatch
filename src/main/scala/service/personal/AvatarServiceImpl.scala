package main.scala.service.personal

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("avatarService")
@Transactional
class AvatarServiceImpl extends AvatarService {
  
}