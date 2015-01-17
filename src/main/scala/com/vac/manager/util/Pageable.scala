package com.vac.manager.util

import scala.beans.BeanProperty

class Pageable {
  @BeanProperty
  var start: Integer = _

  @BeanProperty
  var count: Integer = _
}
