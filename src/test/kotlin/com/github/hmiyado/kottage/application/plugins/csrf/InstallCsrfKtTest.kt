package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.sessions.SessionStorage
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.header
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.just

class InstallCsrfKtTest : DescribeSpec() {
    val ktorListener = KtorApplicationTestListener(beforeSpec = {
        MockKAnnotations.init(this@InstallCsrfKtTest)
        with(application) {
            install(Routing) {
                get("/") { call.respond(HttpStatusCode.OK) }
                delete(Paths.usersIdDelete) { call.respond(HttpStatusCode.OK) }
            }
            install(Sessions) {
                cookie<ClientSession>("client_session", storage = sessionStorage)
                header<CsrfTokenSession>(CustomHeaders.XCSRFToken, storage = sessionStorage)
            }
            install(StatusPages) {
                exception<CsrfTokenException> { call.respond(HttpStatusCode(499, "")) }
            }
            csrf()
        }
    })

    @MockK
    lateinit var sessionStorage: SessionStorage

    override fun listeners(): List<TestListener> = listOf(ktorListener)

    init {
        describe("not csrf target operation") {
            it("should not check csrf token") {
                ktorListener.handleRequest(HttpMethod.Get, "/").run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
        describe("csrf target operation") {
            it("should fail with invalid csrf token") {
                ktorListener.handleRequest(HttpMethod.Delete, Paths.usersIdDelete.assignPathParams(1)).run {
                    response shouldHaveStatus HttpStatusCode(499, "")
                }
            }
            it("should succeed with valid csrf token") {
                val clientSession = ClientSession("")
                coEvery { sessionStorage.read<ClientSession>("client", any()) } returns clientSession
                coEvery {
                    sessionStorage.read<CsrfTokenSession>(
                        "csrf_token",
                        any()
                    )
                } returns CsrfTokenSession(clientSession)
                coEvery { sessionStorage.write(any(), any()) } just Runs
                ktorListener.handleRequest(HttpMethod.Delete, Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader("Cookie", "client_session=client")
                    addHeader("X-CSRF-TOKEN", "csrf_token")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
