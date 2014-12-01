package main.scala.model.generic

import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table
class Avatar(img: Array[Byte]) extends java.io.Serializable {

  @Id
  @SequenceGenerator(name="avatarIdGenerator", sequenceName="avatar_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avatarIdGenerator")
  var avatarId: Long = _
  
  @BeanProperty
  @Column
  var image: Array[Byte] = img

 
}



