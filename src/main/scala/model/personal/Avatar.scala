package main.scala.model.personal

import scala.beans.BeanProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import java.util.Calendar
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.CascadeType
import javax.persistence.FetchType
import main.scala.model.staff.Staff
import javax.persistence.ManyToOne

@Entity
@Table
class Avatar(img: Array[Byte], date: Calendar, st: Staff) extends java.io.Serializable {

  @Id
  @SequenceGenerator(name="avatarIdGenerator", sequenceName="avatar_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avatarIdGenerator")
  var avatarId: Long = _
  
  @BeanProperty
  @Column
  var image: Array[Byte] = img

  @BeanProperty
  @Column
  var uploadDate: Calendar = date
 
  @BeanProperty
  @ManyToOne(optional=false, fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "staffId")
  var staffOwner: Staff = st
  
}



