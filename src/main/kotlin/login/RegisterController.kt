package com.example.login

import com.example.database.tokens.TokenDTO
import com.example.database.tokens.Tokens
import com.example.database.users.UserDTO
import com.example.database.users.Users
import com.example.util.isValidEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.*

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()

        // Проверка email
        if (!registerReceiveRemote.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }

        // Проверка, существует ли пользователь
        val existingUser = Users.fetchUser(registerReceiveRemote.login)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }

        val token = UUID.randomUUID().toString()

        try {
            // Вставка пользователя
            Users.insert(
                UserDTO(
                    login = registerReceiveRemote.login,
                    password = registerReceiveRemote.password,
                    email = registerReceiveRemote.email,
                    username = ""
                )
            )

            // Вставка токена
            Tokens.insert(
                TokenDTO(
                    rowId = UUID.randomUUID().toString(),
                    login = registerReceiveRemote.login,
                    token = token
                )
            )

            call.respond(RegisterResponseRemote(token = token))

        } catch (e: ExposedSQLException) {
            call.respond(HttpStatusCode.InternalServerError, "Database error: ${e.localizedMessage}")
        }
    }
}
