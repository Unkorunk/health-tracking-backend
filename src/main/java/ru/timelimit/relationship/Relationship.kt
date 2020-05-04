package ru.timelimit.relationship

import org.jetbrains.exposed.sql.Table
import ru.timelimit.account.Users

object Relationship : Table("epidemic_db.public.relationship") {
    val patient_id = integer("patient_id").references(Users.id)
    val doctor_id = integer("doctor_id").references(Users.id)
    val status = bool("status")
}