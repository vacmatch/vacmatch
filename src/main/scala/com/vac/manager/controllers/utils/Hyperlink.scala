package com.vac.manager.controllers.utils

import scala.beans.BeanProperty

case class Hyperlink(
  @BeanProperty text: String,
  @BeanProperty href: String,
  @BeanProperty className: String
)
