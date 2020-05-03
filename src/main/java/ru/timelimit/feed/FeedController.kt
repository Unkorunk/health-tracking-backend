package ru.timelimit.feed

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.timelimit.example.HelloWorldController
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
    fun add(@RequestParam("title") title: String,
            @RequestParam("description") description: String,
            @RequestParam("token") token: String) : Map<String, Boolean> {
        val user = AccountUtility.getUserByToken(token) ?: return mapOf(Pair("status", false))

        var status = false
        transaction {
            Feed.insert {
                it[Feed.author_id] = user[HelloWorldController.Users.id]
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
        val user = AccountUtility.getUserByToken(token) ?: return FeedResult(false)

        val posts = mutableListOf<Post>()
        transaction {
            Feed.selectAll().forEach {
                val author = AccountUtility.getUserById(it[Feed.author_id])
                if (author != null) {
                    val authorName =
                        author[HelloWorldController.Users.firstName] + " " + author[HelloWorldController.Users.lastName]
                    posts.add(Post(authorName, it[Feed.title], it[Feed.description], it[Feed.publication_time].toString()))
                }
            }
        }
        return FeedResult(true, posts)
    }
}
