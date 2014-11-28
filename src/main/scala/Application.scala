package main.scala

import org.springframework.context.annotation.Configuration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ImportResource
import org.springframework.boot.SpringApplication

@Configuration
@EnableAutoConfiguration
@ComponentScan
class Application 


object Application extends App {
	SpringApplication.run(classOf[Application]);
}
  
