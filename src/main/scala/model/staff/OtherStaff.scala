package main.scala.model.staff

import javax.persistence.Entity
import javax.persistence.Table
import scala.beans.BeanProperty
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.CascadeType
import javax.persistence.FetchType

@Entity
@Table(name = "OTHER_STAFF")
class OtherStaff {
  
}