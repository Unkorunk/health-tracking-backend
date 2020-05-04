package ru.timelimit.contact

import ru.timelimit.contact.Contacts
import ru.timelimit.contact.Contacts.autoIncrement

import org.jetbrains.exposed.sql.Table
import ru.timelimit.account.Users
import ru.timelimit.feed.Feed
import ru.timelimit.feed.Feed.references

object Contacts : Table("epidemic_db.public.contacts") {
    val id = Contacts.integer("id").autoIncrement()
    val name = Contacts.varchar("name", 64)
    val phone_number = Contacts.varchar("phone_number", 11)
    val status = Contacts.varchar("status", 64)
    val user_id = Feed.integer("user_id").references(Users.id)
}