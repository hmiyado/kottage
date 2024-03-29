package com.github.hmiyado.kottage.helper

import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.plugins.statuspages.statusPages
import com.github.hmiyado.kottage.application.plugins.statuspages.statusPagesModule
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.listeners.TestListener
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.install
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.setBody
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

interface KtorApplicationTest {
    val listener: TestListener

    val usersService: UsersService

    val sessionStorage: SessionStorage

    fun TestApplicationRequest.authorizeAsUser(user: User)

    fun TestApplicationRequest.authorizeAsAdmin(user: User)

    fun setup(with: Application.() -> Unit)

    fun <T : Any, U : Any> install(
        feature: Plugin<Application, T, U>,
        configure: T.() -> Unit,
    )

    fun handleJsonRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {},
    ): TestApplicationCall
}

class KtorApplicationTestDelegate(
    val useDefaultSessionAndAuthentication: Boolean = true,
    val useDefaultStatusPage: Boolean = true,
    val modules: List<Module> = emptyList(),
) : KtorApplicationTest {
    private lateinit var authorizationHelper: AuthorizationHelper

    @MockK
    override lateinit var usersService: UsersService

    @MockK
    lateinit var adminsService: AdminsService

    @MockK
    lateinit var httpClient: HttpClient

    @MockK
    override lateinit var sessionStorage: SessionStorage

    private val ktorListener = KtorApplicationTestListener(
        beforeSpec = {
            with(application) {
                startKoin {
                    modules(
                        statusPagesModule,
                        module {
                            single { httpClient }
                        },
                        *(modules.toTypedArray()),
                    )
                }
                authorizationHelper = AuthorizationHelper(usersService, sessionStorage, adminsService)
                if (useDefaultSessionAndAuthentication) {
                    // authentication should be installed before routing
                    authorizationHelper.installSessionAuthentication(this)
                }
                if (useDefaultStatusPage) {
                    statusPages()
                }
                contentNegotiation()
            }
        },
        afterSpec = {
            stopKoin()
        },
    )

    init {
        MockKAnnotations.init(this)
    }

    override val listener: TestListener
        get() = ktorListener

    override fun TestApplicationRequest.authorizeAsUser(user: User) {
        authorizationHelper.authorizeAsUser(this, user)
    }

    override fun TestApplicationRequest.authorizeAsAdmin(user: User) {
        authorizationHelper.authorizeAsUserAndAdmin(this, user)
    }

    override fun setup(with: Application.() -> Unit) {
        ktorListener.beforeSpecListeners.add {
            with(application)
        }
    }

    override fun <T : Any, U : Any> install(
        feature: Plugin<Application, T, U>,
        configure: T.() -> Unit,
    ) {
        ktorListener.beforeSpecListeners.add {
            application.install(feature, configure)
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

fun KtorApplicationTest.routing(routing: Route.() -> Unit) {
    install(Routing, routing)
}

fun KtorApplicationTest.handleJsonRequest(
    method: HttpMethod,
    uri: String,
    body: String,
    setup: TestApplicationRequest.() -> Unit,
): TestApplicationCall {
    return handleJsonRequest(method, uri) {
        setBody(body)
        setup()
    }
}

fun KtorApplicationTest.handleJsonRequest(
    method: HttpMethod,
    uri: String,
    body: JsonObjectBuilder.() -> Unit,
    setup: TestApplicationRequest.() -> Unit,
): TestApplicationCall = handleJsonRequest(method, uri, buildJsonObject(body).toString(), setup)

fun KtorApplicationTest.get(uri: String, setup: TestApplicationRequest.() -> Unit = {}): TestApplicationCall {
    return handleJsonRequest(HttpMethod.Get, uri, setup)
}

fun KtorApplicationTest.post(
    uri: String,
    body: String,
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Post, uri, body, setup)

fun KtorApplicationTest.post(
    uri: String,
    body: JsonObjectBuilder.() -> Unit = {},
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Post, uri, body, setup)

fun KtorApplicationTest.patch(
    uri: String,
    body: String,
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Patch, uri, body, setup)

fun KtorApplicationTest.patch(
    uri: String,
    body: JsonObjectBuilder.() -> Unit = {},
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Patch, uri, body, setup)

fun KtorApplicationTest.delete(
    uri: String,
    body: String,
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Delete, uri, body, setup)

fun KtorApplicationTest.delete(
    uri: String,
    body: JsonObjectBuilder.() -> Unit = {},
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall = handleJsonRequest(HttpMethod.Delete, uri, body, setup)
