package ru.timelimit.utility

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import ru.timelimit.account.Users


class AccountUtility {
    companion object {
        fun getUserByToken(token: String) : ResultRow? {
            var resultRow: ResultRow? = null
            transaction {
                val result = Users.select { Users.token eq token }

                if (result.empty()) {
                    return@transaction
                }

                result.forEach {
                    val expiresIn = it.getOrNull(Users.expires_in)
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
                val result = Users.select {Users.id eq id }

                if (result.empty()) {
                    return@transaction
                }

                result.forEach {
                    val expiresIn = it.getOrNull(Users.expires_in)
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