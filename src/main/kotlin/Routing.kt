package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserDTO(
    val username: String,
    val password: String,
    val role: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val username: String? = null
)

fun Application.configureRouting() {
    val usersList = loadUsersFromJson()

    routing {
        get("/") {
            call.respondText("Backend de Logue activo")
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val matchedUser = usersList.find {
                it.username == request.username && it.password == request.password
            }

            if (matchedUser != null) {
                call.respond(
                    HttpStatusCode.OK,
                    LoginResponse(
                        success = true,
                        message = "Inicio de sesión exitoso",
                        role = matchedUser.role,
                        username = matchedUser.username
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    LoginResponse(
                        success = false,
                        message = "Credenciales incorrectas"
                    )
                )
            }
        }
    }
}

fun loadUsersFromJson(): List<UserDTO> {
    return try {
        val jsonStream = object {}.javaClass.classLoader.getResourceAsStream("users.json")
        val jsonString = jsonStream?.bufferedReader()?.readText() ?: "[]"
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}