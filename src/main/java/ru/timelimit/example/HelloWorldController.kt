package ru.timelimit.example

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    data class HelloWorldResult(
        val resultStr: String
    )

    @RequestMapping("/example/HelloWorld")
    fun helloWorld(): HelloWorldResult {
        return HelloWorldResult("Hello World")
    }
}
