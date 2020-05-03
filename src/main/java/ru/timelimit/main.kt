package ru.timelimit

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class HealthTrackingBackend

fun main(args: Array<String>) {
    Database.connect(
        "jdbc:postgresql://104.248.59.99:5432/epidemic_db", driver = "org.postgresql.Driver",
        user = "epidemic_user", password = "6QAeE2f8f4tzZ3cGSnLmpaF8"
    )
    runApplication<HealthTrackingBackend>(*args)
}
