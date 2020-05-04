package ru.timelimit.feed

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import ru.timelimit.account.Users

object Feed : Table("epidemic_db.public.feed") {
    val id = integer("id").autoIncrement()
    val author_id = integer("author_id").references(Users.id)
    val patient_id = integer("patient_id").references(Users.id)
    val title = varchar("title", 128)
    val description = text("description")
    val publication_time = datetime("publication_time")
}