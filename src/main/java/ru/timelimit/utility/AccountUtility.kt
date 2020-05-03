package ru.timelimit.utility

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import ru.timelimit.example.HelloWorldController

class AccountUtility {
    companion object {
        fun getUserByToken(token: String) : ResultRow? {
            var resultRow: ResultRow? = null
            transaction {
                val result = HelloWorldController.Users.select { HelloWorldController.Users.token eq token }

                if (result.empty()) {
                    return@transaction
                }

                result.forEach {
                    val expiresIn = it.getOrNull(HelloWorldController.Users.expires_in)
                    if (expiresIn != null && expiresIn >= DateTime.now()) {
                        resultRow = it
                        return@transaction
                    }
                }

                resultRow = null
            }


            return resultRow
        }

        fun getUserById(id: Int) : ResultRow? {
            var resultRow: ResultRow? = null
            transaction {
                val result = HelloWorldController.Users.select { HelloWorldController.Users.id eq id }

                if (result.empty()) {
                    return@transaction
                }

                result.forEach {
                    val expiresIn = it.getOrNull(HelloWorldController.Users.expires_in)
                    if (expiresIn != null && expiresIn >= DateTime.now()) {
                        resultRow = it
                        return@transaction
                    }
                }

                resultRow = null
            }


            return resultRow
        }
    }
}