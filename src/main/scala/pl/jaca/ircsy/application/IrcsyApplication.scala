package pl.jaca.ircsy.application

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class IrcsyApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[IrcsyApplication], args: _*)
  }
}
