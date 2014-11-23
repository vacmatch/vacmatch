package main.scala.model.generic.exceptions

class NotImplementedException(message: String, nestedException: Throwable) extends Exception(message, nestedException) {
  
    def this() = this("NotImplementedException", null)
     
    def this(message: String) = this(message, null)
     
    def this(nestedException : Throwable) = this("", nestedException)
    
}