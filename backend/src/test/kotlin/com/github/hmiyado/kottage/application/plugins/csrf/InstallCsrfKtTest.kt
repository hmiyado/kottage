package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.contentNegotiation
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.authentication.csrf
import com.github.hmiyado.kottage.helper.shouldHaveHeader
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.response.respond
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.header
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.random.Random

class InstallCsrfKtTest : DescribeSpec() {

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var randomGenerator: RandomGenerator

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@InstallCsrfKtTest)
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(
                    module {
                        single { DevelopmentConfiguration.Production }
                        single { randomGenerator }
                    },
                )
            }
        }
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        stopKoin()
    }

    private fun ApplicationTestBuilder.init() {
        application {
            install(Authentication) {
                this.csrf()
            }
            contentNegotiation()
            routing {
                get("/") { call.respond(HttpStatusCode.OK) }
                authenticate("CsrfTokenSession") {
                    delete(Paths.usersIdDelete) { call.respond(HttpStatusCode.OK) }
                }
            }
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage)
                header<CsrfTokenSession>(CustomHeaders.XCSRFToken, storage = sessionStorage)
            }
            csrf()
        }
    }

    init {
        describe("not csrf target operation") {
            it("should not check csrf token") {
                testApplication {
                    init()
                    val response = client.get("/")
                    response.status shouldBe HttpStatusCode.OK
                }
            }
        }
        describe("csrf target operation") {
            context("with invalid custom header") {
                it("should respond Forbidden") {
                    testApplication {
                        init()
                        val response = client.delete(Paths.usersIdDelete.assignPathParams(1))
                        response.status shouldBe HttpStatusCode.Forbidden
                    }
                }
//                it("should respond ${CustomHeaders.XCSRFToken} with token") {
//                    testApplication {
//                        init()
//                        coEvery { randomGenerator.generateString() } returns "token"
//                        val response = client.delete(Paths.usersIdDelete.assignPathParams(1))
//                        response.shouldHaveHeader(HttpHeaders.SetCookie, "")
//                        response.shouldHaveHeader(CustomHeaders.XCSRFToken, "token")
//                    }
//                }
            }
            context("with valid custom header") {
                it("should fail with invalid token") {
                    testApplication {
                        init()
                        coEvery { sessionStorage.read("client") } throws NoSuchElementException("ClientSession not found for 'client'")
                        coEvery {
                            sessionStorage.read("invalid_token")
                        }  throws NoSuchElementException("CsrfTokenSession not found for 'invalid_token'")
                        val response = client.delete(Paths.usersIdDelete.assignPathParams(1)) {
                            header(HttpHeaders.Cookie, "client_session=client")
                            header(CustomHeaders.XCSRFToken, "invalid_token")
                            header(HttpHeaders.Origin, "https://miyado.dev")
                        }
                        response.status shouldBe HttpStatusCode.Forbidden
                    }
                }
                it("should succeed with valid csrf token") {
                    testApplication {
                        init()
                        val clientSession = ClientSession("")
                        coEvery { sessionStorage.read("client") } returns clientSession.toJsonString()
                        coEvery {
                            sessionStorage.read("csrf_token")
                        } returns CsrfTokenSession("csrf_token", clientSession).toJsonString()
                        coEvery { sessionStorage.write(any(), any()) } just Runs

                        val response = client.delete(Paths.usersIdDelete.assignPathParams(1)) {
                            header(HttpHeaders.Cookie, "client_session=client")
                            header(CustomHeaders.XCSRFToken, "csrf_token")
                            header(HttpHeaders.Origin, "https://miyado.dev")
                        }
                        response.status shouldBe HttpStatusCode.OK
                    }
                }
                it("should succeed with valid csrf token and header of case insensitive") {
                    testApplication {
                        init()
                        val clientSession = ClientSession("")
                        coEvery { sessionStorage.read("client") } returns clientSession.toJsonString()
                        coEvery {
                            sessionStorage.read("csrf_token")
                        } returns CsrfTokenSession("csrf_token", clientSession).toJsonString()
                        coEvery { sessionStorage.write(any(), any()) } just Runs

                        val response = client.delete(Paths.usersIdDelete.assignPathParams(1)) {
                            header(HttpHeaders.Cookie, "client_session=client")
                            header(CustomHeaders.XCSRFToken.lowercase(), "csrf_token")
                            header(HttpHeaders.Origin, "https://miyado.dev")
                        }
                        response.status shouldBe HttpStatusCode.OK
                    }
                }
            }
            xit("Origin is checked in CORS so that not check in CSRF") {}
        }
    }
}

private fun ClientSession.toJsonString(): String = Json.encodeToString(this)

private fun CsrfTokenSession.toJsonString(): String = Json.encodeToString(this)
