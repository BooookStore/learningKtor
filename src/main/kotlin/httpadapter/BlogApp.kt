package httpadapter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import domain.snippet.Snippet
import domain.snippet.nextSnippetId
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.util.*

open class SimpleJWT(secret: String) {

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun sign(name: String): String = JWT.create()
        .withClaim("name", name)
        .sign(algorithm)

}

class User(val name: String, val password: String)

val users: MutableMap<String, User> = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

class LoginRegister(val user: String, val password: String)

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

val snippet: MutableList<Snippet> = Collections.synchronizedList(
    mutableListOf(
        Snippet(nextSnippetId(), user = "test", text = "hello"),
        Snippet(nextSnippetId(), user = "test", text = "world")
    )
)

class InvalidCredentialsException(message: String) : RuntimeException(message)

fun Application.main() {
    val simpleJWT = SimpleJWT("my-super-secret-for-jwt")
    embeddedServer(Netty, 8080) {
        install(StatusPages) {
            exception<InvalidCredentialsException> { exception ->
                call.respond(
                    HttpStatusCode.Unauthorized, mapOf(
                        "OK" to false,
                        "error" to (exception.message ?: "")
                    )
                )
            }
        }
        install(Authentication) {
            basic(name = "myauth1") {
                realm = "myrealm"
                validate { if (it.name == "user" && it.password == "password") UserIdPrincipal("user") else null }
            }
            jwt(name = "myjwt1") {
                verifier(simpleJWT.verifier)
                validate {
                    UserIdPrincipal(it.payload.getClaim("name").asString())
                }
            }
        }
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        routing {
            post("/login-register") {
                val post = call.receive<LoginRegister>()
                val user = users.getOrPut(post.user) { User(post.user, post.password) }
                if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
                call.respond(mapOf("token" to simpleJWT.sign(user.name)))
            }
            get("/") {
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
            route("/snippets") {
                get {
                    call.respond(mapOf("snippet" to synchronized(snippet) {
                        snippet.toList()
                    }))
                }
                authenticate("myjwt1") {
                    post {
                        val post = call.receive<PostSnippet>()
                        val principal = call.principal<UserIdPrincipal>() ?: error("No principal")

                        snippet += Snippet(nextSnippetId(), principal.name, post.snippet.text)
                        call.respond(mapOf("OK" to true))
                    }
                }
            }
        }
    }.start(wait = true)
}