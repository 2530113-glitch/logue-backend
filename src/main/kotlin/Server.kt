import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class User(val username: String, val password: String, val role: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val username: String? = null
)

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Get)
        }

        install(ContentNegotiation) {
            json()
        }

        routing {
            post("/login") {
                val request = call.receive<LoginRequest>()

                // Lee el archivo users.json de la carpeta resources
                val jsonStream = object {}.javaClass.getResourceAsStream("/users.json")

                if (jsonStream == null) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        LoginResponse(success = false, message = "No se encontró el archivo users.json en el servidor")
                    )
                    return@post
                }

                val jsonString = jsonStream.bufferedReader().use { it.readText() }
                val users = Json.decodeFromString<List<User>>(jsonString)

                // Busca las credenciales ingresadas
                val matchedUser = users.find {
                    it.username == request.username && it.password == request.password
                }

                if (matchedUser != null) {
                    call.respond(
                        LoginResponse(
                            success = true,
                            message = "¡Bienvenido, ${matchedUser.username}!",
                            role = matchedUser.role,
                            username = matchedUser.username
                        )
                    )
                } else {
                    call.respond(
                        LoginResponse(
                            success = false,
                            message = "Usuario o contraseña incorrectos"
                        )
                    )
                }
            }
        }
    }.start(wait = true)
}