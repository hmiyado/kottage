package com.github.hmiyado.kottage.application.plugins.authentication

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.authorizeAsUserAndAdmin
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.model.User
import com.github.hmiyado.kottage.model.UserSession
import com.github.hmiyado.kottage.service.users.UsersService
import com.github.hmiyado.kottage.service.users.admins.AdminsService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
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
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK

class AuthenticationConfigurationKtTest : DescribeSpec() {
    @MockK
    private lateinit var adminsService: AdminsService

    @MockK
    private lateinit var usersService: UsersService

    @MockK
    private lateinit var sessionStorage: SessionStorage

    private lateinit var authorizationHelper: AuthorizationHelper

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@AuthenticationConfigurationKtTest)
        authorizationHelper = AuthorizationHelper(usersService, sessionStorage)
    }

    private val init: ApplicationTestBuilder.() -> Unit = {
        application {
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
    }

    init {
        describe("admin") {
            it("should login as admin") {
                testApplication {
                    init()
                    val admin = User(id = 10)
                    
                    val response = client.get("/") {
                        authorizeAsUserAndAdmin(authorizationHelper, admin)
                    }
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }

            it("should not login as admin") {
                testApplication {
                    init()
                    
                    val response = client.get("/")
                    response shouldHaveStatus HttpStatusCode.Unauthorized
                }
            }
        }
    }
}
