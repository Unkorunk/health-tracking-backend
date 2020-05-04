package ru.timelimit.account

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object Users : Table("epidemic_db.public.Users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 64)
    val password = varchar("password", 64)
    val role = bool("role")
    val firstName = varchar("firstName", 64)
    val lastName = varchar("lastName", 64)
    val token = varchar("token", 128)
    val expires_in = datetime("expires_in")
}
