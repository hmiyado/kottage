package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import io.kotest.core.Tuple2
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
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class InstallCsrfKtTest : DescribeSpec() {

    @MockK
    lateinit var sessionStorage: SessionStorage

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@InstallCsrfKtTest)
        startKoin {
            modules(
                module {
                    single { DevelopmentConfiguration.Production }
                },
            )
        }
    }

    override fun afterTest(f: suspend (Tuple2<TestCase, TestResult>) -> Unit) {
        super.afterTest(f)
        stopKoin()
    }

    override suspend fun afterTest(testCase: TestCase, result: io.kotest.core.test.TestResult) {
        super.afterTest(testCase, result)
        stopKoin()
    }

    private fun ApplicationTestBuilder.init() {
        application {
            routing {
                get("/") { call.respond(HttpStatusCode.OK) }
                delete(Paths.usersIdDelete) { call.respond(HttpStatusCode.OK) }
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
            it("should fail with invalid csrf token") {
                testApplication {
                    init()
                    val response = client.delete(Paths.usersIdDelete.assignPathParams(1))
                    response.status shouldBe HttpStatusCode(498, "")
                }
            }
            it("should fail with invalid Origin") {
                testApplication {
                    init()
                    val clientSession = ClientSession("")
                    coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                    coEvery {
                        sessionStorage.read("csrf_token")
                    } returns "associatedClientRepresentation=#s${clientSession.token}"
                    coEvery { sessionStorage.write(any(), any()) } just Runs

                    val response = client.delete(Paths.usersIdDelete.assignPathParams(1)) {
                        header(HttpHeaders.Cookie, "client_session=client")
                        header(CustomHeaders.XCSRFToken, "csrf_token")
                        header(HttpHeaders.Origin, "http://invalid.origin")
                    }
                    response.status shouldBe HttpStatusCode(497, "")
                }
            }
            it("should succeed with valid csrf token") {
                testApplication {
                    init()
                    val clientSession = ClientSession("")
                    coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                    coEvery {
                        sessionStorage.read("csrf_token")
                    } returns "associatedClientRepresentation=#s${clientSession.token}"
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
                    coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                    coEvery {
                        sessionStorage.read("csrf_token")
                    } returns "associatedClientRepresentation=#s${clientSession.token}"
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
    }
}
