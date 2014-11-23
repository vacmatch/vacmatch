package main.scala

import org.springframework.context.support.ClassPathXmlApplicationContext

class Main {

  def main(args: List[String]) = {
    new Main().start()
  }
  
  def start() = {
    
    // open/read the application context file
    var ctx: ClassPathXmlApplicationContext = new ClassPathXmlApplicationContext("application.xml")
    
  }


}
