package xyz.nietongxue.app.develop;

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


fun main() {
    runApplication<Application>()
}


@SpringBootApplication(scanBasePackages = ["xyz.nietongxue.kanglang","xyz.nietongxue.app.develop"])
class Application() {


}


