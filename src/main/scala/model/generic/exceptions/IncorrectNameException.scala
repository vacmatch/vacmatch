package main.scala.model.generic.exceptions

class IncorrectNameException(message: String, nestedException: Throwable) extends Exception(message, nestedException) {

  def this() = this("IncorrectNameException", null)

  def this(message: String) = this(message, null)

  def this(nestedException: Throwable) = this("", nestedException)

}
