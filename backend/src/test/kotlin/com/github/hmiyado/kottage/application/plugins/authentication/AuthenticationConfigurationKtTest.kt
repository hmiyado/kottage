package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK

class AuthenticationConfigurationKtTest : DescribeSpec() {
    @MockK
    private lateinit var adminsService: AdminsService

    @MockK
    private lateinit var usersService: UsersService

    @MockK
    private lateinit var sessionStorage: SessionStorage

    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@AuthenticationConfigurationKtTest)
        with(application) {
            install(Sessions) {
                cookie<UserSession>("user_session", storage = sessionStorage)
            }
            install(Authentication) {
                admin(usersService, adminsService)
            }
            routing {
                authenticate("admin") {
                    get("/") {
                        call.authentication.principal<UserPrincipal.Admin>() ?: kotlin.run {
                            call.respond(HttpStatusCode.Unauthorized)
                            return@get
                        }
                        call.respond("OK")
                    }
                }
            }
        }
    })

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("admin") {
            it("should login as admin") {
                val admin = User(id = 10)
                ktorListener.handleJsonRequest(HttpMethod.Get, "/") {
                    AuthorizationHelper.authorizeAsUserAndAdmin(
                        this,
                        sessionStorage,
                        usersService,
                        adminsService,
                        admin
                    )
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should not login as admin") {
                ktorListener.handleJsonRequest(HttpMethod.Get, "/") {
                    // no authentication
                }.run {
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
