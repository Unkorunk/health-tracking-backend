package ru.timelimit.feed

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.account.Users
import ru.timelimit.relationship.Relationship
import ru.timelimit.utility.AccountUtility

@RestController
class FeedController {
    data class Post(
        val author_name: String,
        val title: String,
        val description: String,
        val publication_time: String
    )

    data class FeedResult(
        val status: Boolean,
        val posts: List<Post> = listOf()
    )

    @RequestMapping("feed/add")
    fun add(@RequestParam("patientId") patientId: Int,
            @RequestParam("title") title: String,
            @RequestParam("description") description: String,
            @RequestParam("token") token: String) : Map<String, Boolean> {
        val doctorProfile = AccountUtility.getUserByToken(token) ?: return mapOf(Pair("status", false))
        if (!doctorProfile[Users.role]) return mapOf(Pair("status", false))

        val patientProfile = AccountUtility.getUserById(patientId) ?: return mapOf(Pair("status", false))
        if (patientProfile[Users.role]) return mapOf(Pair("status", false))

        var status = false
        transaction {
            val relationship = Relationship.select {
            (Relationship.doctor_id eq doctorProfile[Users.id]) and
                    (Relationship.patient_id eq patientId) and Relationship.status }
            status = relationship.empty()
        }
        if (status) return mapOf(Pair("status", false))

        status = false
        transaction {
            Feed.insert {
                it[Feed.author_id] = doctorProfile[Users.id]
                it[Feed.patient_id] = patientId
                it[Feed.title] = title
                it[Feed.description] = description
                it[Feed.publication_time] = DateTime.now()
            }
            status = true
        }

        return mapOf(Pair("status", status))
    }

    @RequestMapping("feed/get")
    fun get(@RequestParam("token") token: String) : FeedResult {
        val patientProfile = AccountUtility.getUserByToken(token) ?: return FeedResult(false)
        if (patientProfile[Users.role]) return FeedResult(false)

        val posts = mutableListOf<Post>()
        transaction {
            Feed.select { Feed.patient_id eq patientProfile[Users.id] }.forEach {
                val author = AccountUtility.getUserById(it[Feed.author_id])
                if (author != null) {
                    val authorName = author[Users.firstName] + " " + author[Users.lastName]
                    posts.add(Post(authorName, it[Feed.title], it[Feed.description], it[Feed.publication_time].toString()))
                }
            }
        }
        return FeedResult(true, posts)
    }
}
