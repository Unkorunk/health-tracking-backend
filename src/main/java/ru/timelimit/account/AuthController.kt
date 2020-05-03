package ru.timelimit.account

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.example.HelloWorldController
import java.util.*

data class Response(val status: Boolean, val result: String, val message: String? = null)

@RestController
internal class AuthController {
    data class RegistrationResult(
        val status: Boolean
    )
    @RequestMapping("/account/registration")
    fun registration(@RequestParam("login") login: String, @RequestParam("password") password: String,
                     @RequestParam("role", defaultValue = "0") role: Int,
                     @RequestParam("firstName", defaultValue = "0") firstName: String,
                     @RequestParam("lastName", defaultValue = "no") lastName: String,
                     @RequestParam("token", defaultValue = "no") token: String,
                     @RequestParam("expires_in", defaultValue = "no") expires_in: String): RegistrationResult {

        var status = false

        transaction {
            addLogger ( StdOutSqlLogger )

            val req = Users.select { Users.username eq login }
            if (req.count() == 0L) {
                val stPeteId =  Users.insert {
                    it [username] = login
                    it [Users.password] = password
                    it [Users.role] = role
                    it [Users.firstName] = firstName
                    it [Users.lastName] = lastName
                    it [Users.token] = token
                }
                status = true
            }
        }
        return RegistrationResult(status)
    }

}