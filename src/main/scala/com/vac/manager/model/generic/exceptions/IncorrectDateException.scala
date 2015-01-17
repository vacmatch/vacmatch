package com.vac.manager.model.generic.exceptions

class IncorrectDateException(message: String, nestedException: Throwable) extends Exception(message, nestedException) {

  def this() = this("IncorrectDateException", null)

  def this(message: String) = this(message, null)

  def this(nestedException: Throwable) = this("", nestedException)

}
