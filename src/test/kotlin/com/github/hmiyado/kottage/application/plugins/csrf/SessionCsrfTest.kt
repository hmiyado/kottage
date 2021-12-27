package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.header
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify

class SessionCsrfTest : DescribeSpec() {
    val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@SessionCsrfTest)
        with(application) {
            install(Routing) {
                get("/") { }
                post("/") { }
            }
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage) {
                }
                header<CsrfTokenSession<ClientSession>>("X-CSRF-TOKEN", storage = sessionStorage) {
                }
            }
            install(Csrf) {
                session<ClientSession> {
                    onFail { token ->
                        onFailFunction(token)
                    }
                }
            }
        }
    })

    @MockK
    lateinit var sessionStorage: SessionStorage

    @MockK
    lateinit var onFailFunction: (CsrfTokenSession<ClientSession>?) -> Unit

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    override fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        clearAllMocks()
    }

    init {
        describe("no or invalid client session, no or invalid csrf token") {
            it("should fail with no client session") {
                coEvery {
                    sessionStorage.read<ClientSession>(
                        any(),
                        any()
                    )
                } throws NoSuchElementException("client_session")
                coEvery {
                    sessionStorage.read<CsrfTokenSession<ClientSession>>(
                        any(),
                        any()
                    )
                } throws NoSuchElementException("X-CSRF-TOKEN")
                every { onFailFunction(any()) } just Runs

                ktorListener.handleRequest(HttpMethod.Get, "/")
                verify { onFailFunction.invoke(null) }
            }
            it("should fail with no client session but only empty cookie") {
                coEvery {
                    sessionStorage.read<ClientSession>(
                        any(),
                        any()
                    )
                } throws NoSuchElementException("client_session")
                coEvery {
                    sessionStorage.read<CsrfTokenSession<ClientSession>>(
                        any(),
                        any()
                    )
                } throws NoSuchElementException("X-CSRF-TOKEN")
                every { onFailFunction(any()) } just Runs

                ktorListener.handleRequest(HttpMethod.Get, "/") {
                    addHeader("Cookie", "client_session=")
                }
                verify { onFailFunction.invoke(null) }
            }
        }
        describe("valid client session, no or invalid csrf token") {
            it("should fail with valid client session but no csrf token") {
                val clientSession = ClientSession("session")
                coEvery { sessionStorage.read<ClientSession>(clientSession.value, any()) } returns clientSession
                coEvery {
                    sessionStorage.read<CsrfTokenSession<ClientSession>>(
                        not(clientSession.value),
                        any()
                    )
                } throws NoSuchElementException("X-CSRF-TOKEN")
                coEvery { sessionStorage.write(any(), any()) } just Runs
                every { onFailFunction(any()) } just Runs

                ktorListener.handleRequest(HttpMethod.Get, "/") {
                    addHeader("Cookie", "client_session=${clientSession.value}")
                }
                verify { onFailFunction.invoke(ofType()) }
            }
            it("should fail with valid client session but invalid csrf token") {
                val clientSession = ClientSession("session")
                val csrfToken = "invalid_csrf_token"
                val csrfTokenSession = CsrfTokenSession(ClientSession("invalid_session"))
                coEvery { sessionStorage.read<ClientSession>(clientSession.value, any()) } returns clientSession
                coEvery {
                    sessionStorage.read<CsrfTokenSession<ClientSession>>(
                        csrfToken,
                        any()
                    )
                } returns csrfTokenSession
                coEvery { sessionStorage.write(any(), any()) } just Runs
                every { onFailFunction(any()) } just Runs

                ktorListener.handleRequest(HttpMethod.Get, "/") {
                    addHeader("Cookie", "client_session=${clientSession.value}")
                    addHeader("X-CSRF-Token", csrfToken)
                }
                verify { onFailFunction.invoke(ofType()) }
            }
        }
        describe("valid client session, valid csrf token") {
            it("should be success with valid csrf token session, valid client session") {
                val clientSession = ClientSession("session")
                val csrfToken = "csrf-token"
                val expected = CsrfTokenSession(clientSession)
                coEvery { sessionStorage.read<ClientSession>(clientSession.value, any()) } returns clientSession
                coEvery { sessionStorage.read<CsrfTokenSession<ClientSession>>(csrfToken, any()) } returns expected
                coEvery { sessionStorage.write(any(), any()) } just Runs
                every { onFailFunction(any()) } just Runs

                ktorListener.handleRequest(HttpMethod.Get, "/") {
                    addHeader("Cookie", "client_session=${clientSession.value}")
                    addHeader("X-CSRF-TOKEN", csrfToken)
                }
                verify(exactly = 0) { onFailFunction.invoke(any()) }
            }
        }
    }

    data class ClientSession(
        val value: String
    )

}
