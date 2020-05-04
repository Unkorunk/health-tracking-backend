package ru.timelimit.contact

import org.jetbrains.exposed.sql.Table
import ru.timelimit.account.Users

object Contacts : Table("epidemic_db.public.contacts") {
    val id = Contacts.integer("id").autoIncrement()
    val name = Contacts.varchar("name", 64)
    val phone_number = Contacts.varchar("phone_number", 11)
    val status = Contacts.bool("status")
    val user_id = Contacts.integer("user_id").references(Users.id)
}