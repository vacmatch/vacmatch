package com.vac.manager.controllers.utils

import scala.beans.BeanProperty
import com.vac.manager.controllers.actionable.ActionableSoccerStaffStats
import scala.collection.JavaConverters._
import javax.validation.Valid

class ThymeleafList[T](l: java.util.List[T]) {

  @BeanProperty
  @Valid
  var list: java.util.List[T] = l

  def this() = this(List().asJava)

  override def toString(): String = {
    "{THYMELEAF LIST" +
      "\nlist: " + list +
      "}"
  }

}
