package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.header
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import org.koin.core.context.startKoin
import org.koin.dsl.module

class InstallCsrfKtTest : DescribeSpec(), KtorApplicationTest by KtorApplicationTestDelegate(
    useDefaultSessionAndAuthentication = false,
    useDefaultStatusPage = false,
) {

    override fun listeners(): List<TestListener> = listOf(listener)

    init {
        MockKAnnotations.init(this)
        install(Routing) {
            get("/") { call.respond(HttpStatusCode.OK) }
            delete(Paths.usersIdDelete) { call.respond(HttpStatusCode.OK) }
        }
        install(Sessions) {
            cookie<ClientSession>("client_session", storage = sessionStorage)
            header<CsrfTokenSession>(CustomHeaders.XCSRFToken, storage = sessionStorage)
        }
        install(StatusPages) {
            exception<CsrfHeaderException> { call.respond(HttpStatusCode(499, "")) }
            exception<CsrfTokenException> { call.respond(HttpStatusCode(498, "")) }
            exception<CsrfOriginException> { call.respond(HttpStatusCode(497, "")) }
        }
        setup {
            startKoin {
                modules(
                    module { single { DevelopmentConfiguration.Production } }
                )
            }
            csrf()
        }

        describe("not csrf target operation") {
            it("should not check csrf token") {
                get("/").run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
        describe("csrf target operation") {
            it("should fail with invalid csrf token") {
                delete(Paths.usersIdDelete.assignPathParams(1)).run {
                    response shouldHaveStatus HttpStatusCode(498, "")
                }
            }
            it("should fail with invalid Origin") {
                val clientSession = ClientSession("")
                coEvery { sessionStorage.read<ClientSession>("client", any()) } returns clientSession
                coEvery {
                    sessionStorage.read<CsrfTokenSession>("csrf_token", any())
                } returns CsrfTokenSession(clientSession)
                coEvery { sessionStorage.write(any(), any()) } just Runs
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader("Cookie", "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken, "csrf_token")
                    addHeader("Origin", "http://invalid.origin")
                }.run {
                    response shouldHaveStatus HttpStatusCode(497, "")
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
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader("Cookie", "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken, "csrf_token")
                    addHeader("Origin", "https://miyado.dev")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
            it("should succeed with valid csrf token and header of case insensitive") {
                val clientSession = ClientSession("")
                coEvery { sessionStorage.read<ClientSession>("client", any()) } returns clientSession
                coEvery {
                    sessionStorage.read<CsrfTokenSession>(
                        "csrf_token",
                        any()
                    )
                } returns CsrfTokenSession(clientSession)
                coEvery { sessionStorage.write(any(), any()) } just Runs
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader("Cookie", "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken.lowercase(), "csrf_token")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
