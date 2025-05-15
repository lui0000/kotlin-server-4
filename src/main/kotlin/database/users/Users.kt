package com.example.database.users

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val login = varchar("login", 25)
    val password = varchar("password", 25)
    val username = varchar("username", 30)
    val email = varchar("email", 25)

    fun insert(userDTO: UserDTO) {
        transaction {
            Users.insert {
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[username] = userDTO.username
                it[email] = userDTO.email ?: ""
            }
        }
    }

    fun fetchUser(login: String): UserDTO? {
        return try {
            transaction {
                Users.selectAll().where { Users.login eq login }.singleOrNull()?.let { row ->
                    UserDTO(
                        login = row[Users.login],
                        password = row[password],
                        username = row[username],
                        email = row[email]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
