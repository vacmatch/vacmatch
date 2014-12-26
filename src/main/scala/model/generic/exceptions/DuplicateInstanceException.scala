package main.scala.model.generic.exceptions

class DuplicateInstanceException(message: String, nestedException: Throwable) extends Exception(message, nestedException) {

  def this() = this("DuplicateInstanceException", null)

  def this(message: String) = this(message, null)

  def this(nestedException: Throwable) = this("", nestedException)

}
