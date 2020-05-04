package ru.timelimit.account

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.account.Users.token

import java.util.*
import kotlin.streams.asSequence

data class Response(val status: Boolean, val result: String, val message: String? = null)

@RestController
internal class AuthController {
    data class RegistrationResult(
        val status: Boolean
    )

    data class LoginResult(
        val status: Boolean,
        val token: String,
        val expires_in: String
    )

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    @RequestMapping("/account/registration")
    fun registration(@RequestParam("login") login: String, @RequestParam("password") password: String,
                     @RequestParam("role") role: Int,
                     @RequestParam("firstName") firstName: String,
                     @RequestParam("lastName") lastName: String,
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



    @RequestMapping("/account/login")
    fun login(@RequestParam("login") login: String, @RequestParam("password") password: String): LoginResult {
        var status = false

        var randomString: String = ""
        var dateTime: DateTime = DateTime.now()
        transaction {
            addLogger ( StdOutSqlLogger )

            val req = Users.select { Users.username eq login }
            
            if (req.count() == 1L && req.single()[Users.password] == password) {
                var uniq: Long
                do {
                    randomString = (1..128)
                            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                            .map(charPool::get)
                            .joinToString("")
                    uniq = Users.select{ Users.token eq randomString }.count();
                } while ( uniq != 0L)
                
                dateTime = DateTime.now().plusMinutes(10)

                Users.update ({ Users.username eq login }) {
                    it[Users.token] = randomString
                    it[Users.expires_in] = dateTime
                }

                status = true
            }
        }

        return LoginResult(status, randomString, dateTime.toString() )
    }

}