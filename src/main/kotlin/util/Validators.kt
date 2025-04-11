package com.example.util
import com.example.cache.InMemoryCache
import com.example.cache.TokenCache
import com.example.login.RegisterReceiveRemote
import com.example.login.RegisterResponseRemote
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import java.util.UUID

fun Application.configureRegisterRouting() {
    routing {
        post("path") {
            val receive = call.receive<RegisterReceiveRemote>()

            if (!receive.email.isValidEmail()) {
                call.respond(HttpStatusCode.BadRequest, message = "Email is not valid")
            }

            if (InMemoryCache.userList.map { it.login }.contains(receive.login)) {
                call.respond(HttpStatusCode.Conflict, message = "User already exists")
            }

            val token = UUID.randomUUID().toString()
            InMemoryCache.userList.add(receive)
            InMemoryCache.token.add(TokenCache(login = receive.login, token = token))

            call.respond(RegisterResponseRemote(token = token))


        }
    }
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))
}
