package com.example

import com.example.login.configureLoginRouting
import com.example.login.configureRegisterRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Подключение к базе данных PostgreSQL
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/mobile_5",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "123"
    )

    configureSerialization()
    configureRouting()
    configureRegisterRouting()
    configureLoginRouting()
}

