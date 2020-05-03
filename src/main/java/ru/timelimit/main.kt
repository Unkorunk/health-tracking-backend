package ru.timelimit

import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class HealthTrackingBackend

fun main(args: Array<String>) {
    runApplication<HealthTrackingBackend>(*args)
}
