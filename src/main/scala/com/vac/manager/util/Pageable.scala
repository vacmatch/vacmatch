package com.vac.manager.util

import scala.beans.BeanProperty

class Pageable {
  @BeanProperty
  var start: Int = _

  @BeanProperty
  var count: Int = _
}
