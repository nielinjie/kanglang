package xyz.nietongxue.app.reviewer;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


fun main() {
    runApplication<Application>()
}


@SpringBootApplication(scanBasePackages = ["xyz.nietongxue.kanglang", "xyz.nietongxue.app.reviewer"])
class Application() {


}


