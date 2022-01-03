package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.UsersService
import io.kotest.core.listeners.TestListener
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.setBody
import io.ktor.sessions.SessionStorage
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

interface KtorApplicationTest {
    val listener: TestListener

    val usersService: UsersService

    fun TestApplicationRequest.authorizeAsAdmin(user: User)

    fun routing(routing: Route.() -> Unit)

    fun handleJsonRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {}
    ): TestApplicationCall
}

class KtorApplicationTestDelegate() : KtorApplicationTest {
    private lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    override lateinit var usersService: UsersService

    @MockK
    lateinit var sessionStorage: SessionStorage

    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
        RoutingTestHelper.setupRouting(application, {
            authorizationHelper.installSessionAuthentication(it)
        })
    })

    init {
        MockKAnnotations.init(this)
    }

    override val listener: TestListener
        get() = ktorListener

    override fun TestApplicationRequest.authorizeAsAdmin(user: User) {
        authorizationHelper.authorizeAsUserAndAdmin(this, user)
    }

    override fun routing(routing: Route.() -> Unit) {
        ktorListener.beforeSpecListeners.add {
            application.routing(routing)
        }
    }

    override fun handleJsonRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit,
    ): TestApplicationCall {
        return ktorListener.handleJsonRequest(method, uri, setup)
    }
}

fun KtorApplicationTest.get(uri: String, setup: TestApplicationRequest.() -> Unit = {}): TestApplicationCall {
    return handleJsonRequest(HttpMethod.Get, uri, setup)
}

fun KtorApplicationTest.post(
    uri: String,
    body: JsonObjectBuilder.() -> Unit = {},
    setup: TestApplicationRequest.() -> Unit = {}
): TestApplicationCall {
    return handleJsonRequest(HttpMethod.Post, uri) {
        setBody(buildJsonObject(body).toString())
        setup()
    }
}
