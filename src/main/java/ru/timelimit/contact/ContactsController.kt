package ru.timelimit.contact

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.account.Users


import ru.timelimit.utility.AccountUtility

@RestController
class ContactsController {
    data class Contact(
            val contact_name: String,
            val phone_number: String,
            val status: Boolean
    )

    data class GetContactsResult(
            val status: Boolean,
            val contacts: List<Contact> = listOf()
    )

    fun CheckNumberValidation(phone_number: String) : Boolean {
        val pattern = ("^7\\d{10}$").toRegex()

        return pattern.matches(phone_number)
    }

    @RequestMapping("contacts/add")
    fun add(@RequestParam("name") name: String,
            @RequestParam("phone_number") phone: String,
            @RequestParam("status", defaultValue = false.toString()) contact_status: Boolean,
            @RequestParam("token") token: String) : Map<String, Boolean> {
        val user = AccountUtility.getUserByToken(token) ?: return mapOf(Pair("status", false))

        if (!CheckNumberValidation(phone)) return mapOf(Pair("status", false))
        var status = false
        transaction {
            val req = Contacts.select { Contacts.name eq name }
            if (req.count() == 0L) {
                Contacts.insert {
                    it[Contacts.name] = name
                    it[phone_number] = phone
                    it[Contacts.status] = contact_status
                    it[user_id] = user[Users.id]
                }
                status = true
            }
        }

        return mapOf(Pair("status", status))
    }

    @RequestMapping("contacts/get")
    fun get(@RequestParam("token") token: String) : ContactsController.GetContactsResult {
        val user = AccountUtility.getUserByToken(token) ?: return ContactsController.GetContactsResult(false)

        val contacts = mutableListOf<ContactsController.Contact>()
        transaction {
            Contacts.selectAll().forEach {

                contacts.add(ContactsController.Contact(it[Contacts.name],
                            it[Contacts.phone_number], it[Contacts.status]))

            }
        }
        return ContactsController.GetContactsResult(true, contacts.toList())
    }

}