package ru.timelimit.example

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    data class HelloWorldResult(
        val resultStr: String
    )

    data class LoginResult(
        val status: Boolean,
        val token: String
    )


    object Users : Table("epidemic_db.public.Users") {
        val id = integer("id")
        val username = varchar("username", 64)
        val password = varchar("password", 64)
        val role = integer("role")
        val firstName = varchar("firstName", 64)
        val lastName = varchar("lastName", 64)
    }

    @RequestMapping("/example/HelloWorld")
    fun helloWorld(): HelloWorldResult {
        return HelloWorldResult("Hello World")
    }

    @RequestMapping("/account/login")
    fun login(@RequestParam("login") login: String, @RequestParam("password") password: String)
            : LoginResult {
        var status = false

        transaction {
            val req = Users.select { Users.username eq login }
            if (req.count() == 1L && req.single()[Users.password] == password) {
                status = true
            }
        }

        return LoginResult(status, "TODO")
    }
}
