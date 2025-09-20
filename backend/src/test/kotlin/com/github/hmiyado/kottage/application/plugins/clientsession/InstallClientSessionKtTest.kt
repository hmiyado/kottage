package com.github.hmiyado.kottage.application.plugins.clientsession

import com.github.hmiyado.kottage.helper.shouldHaveCookie
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.haveMinLength
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class InstallClientSessionKtTest : DescribeSpec() {
    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var randomGenerator: RandomGenerator

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        MockKAnnotations.init(this@InstallClientSessionKtTest)
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(
                    module {
                        single { randomGenerator }
                    },
                )
            }
        }
    }

    override suspend fun afterTest(
        testCase: TestCase,
        result: TestResult,
    ) {
        super.afterTest(testCase, result)
        stopKoin()
    }

    private fun ApplicationTestBuilder.init() {
        application {
            routing {
                get("/") { call.respond(HttpStatusCode.OK, "{}") }
                post("/test") { call.respond(HttpStatusCode.OK, "{}") }
            }
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage)
            }
            clientSession()
        }
    }

    init {
        describe("ClientSession Plugin") {
            context("when no existing session") {
                it("should create new ClientSession for GET request") {
                    testApplication {
                        init()
                        coEvery { randomGenerator.generateString() } returns "new_session_id"
                        coEvery { sessionStorage.read(any()) } throws NoSuchElementException("")
                        coEvery { sessionStorage.write(any(), any()) } just Runs

                        val response = client.get("/")

                        response.status shouldBe HttpStatusCode.OK
                        response.shouldHaveCookie("client_session", haveMinLength(1))
                    }
                }

                it("should create new ClientSession for POST request") {
                    testApplication {
                        init()
                        coEvery { randomGenerator.generateString() } returns "new_session_id"
                        coEvery { sessionStorage.write(any(), any()) } just Runs

                        val response = client.post("/test")

                        response.status shouldBe HttpStatusCode.OK
                        response.shouldHaveCookie("client_session", haveMinLength(1))
                    }
                }
            }

            context("when existing session") {
                it("should not create new session when ClientSession already exists") {
                    testApplication {
                        init()
                        val existingSession = ClientSession("existing_token")
                        coEvery { sessionStorage.read("existing") } returns existingSession.toJsonString()

                        val response =
                            client.get("/") {
                                headers.append(HttpHeaders.Cookie, "client_session=existing")
                            }

                        response.status shouldBe HttpStatusCode.OK
                        // Should not set new cookie when session already exists
                        response.headers[HttpHeaders.SetCookie] shouldBe null
                    }
                }

                it("should use existing session in subsequent calls") {
                    testApplication {
                        init()
                        val existingSession = ClientSession("existing_session_token")
                        coEvery { sessionStorage.read("existing") } returns existingSession.toJsonString()

                        val response =
                            client.post("/test") {
                                headers.append(HttpHeaders.Cookie, "client_session=existing")
                            }

                        response.status shouldBe HttpStatusCode.OK
                    }
                }
            }
        }
    }
}
