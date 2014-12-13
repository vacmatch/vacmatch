package main.scala.model.federation

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
@Table(name = "FEDERATION")
class Federation {
  
  @Id
  @SequenceGenerator(name="federationIdGenerator", sequenceName="federation_id_seq")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "federationIdGenerator")
  var fedId: Long = _
  
}