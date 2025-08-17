package com.github.hmiyado.kottage.application.plugins.csrf

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.helper.KtorApplicationTest
import com.github.hmiyado.kottage.helper.KtorApplicationTestDelegate
import com.github.hmiyado.kottage.helper.delete
import com.github.hmiyado.kottage.helper.get
import com.github.hmiyado.kottage.helper.shouldHaveStatus
import com.github.hmiyado.kottage.openapi.Paths
import com.github.hmiyado.kottage.route.assignPathParams
import io.github.hmiyado.ktor.csrfprotection.CsrfTokenSession
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.header
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import org.koin.dsl.module

class InstallCsrfKtTest :
    DescribeSpec(),
    KtorApplicationTest by KtorApplicationTestDelegate(
        useDefaultSessionAndAuthentication = false,
        useDefaultStatusPage = false,
        modules = listOf(module { single { DevelopmentConfiguration.Production } }),
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
            exception<CsrfHeaderException> { call, _ -> call.respond(HttpStatusCode(499, "")) }
            exception<CsrfTokenException> { call, _ -> call.respond(HttpStatusCode(498, "")) }
            exception<CsrfOriginException> { call, _ -> call.respond(HttpStatusCode(497, "")) }
        }
        setup {
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
                coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                coEvery {
                    sessionStorage.read("csrf_token")
                } returns "associatedClientRepresentation=#s${clientSession.token}"
                coEvery { sessionStorage.write(any(), any()) } just Runs
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader(HttpHeaders.Cookie, "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken, "csrf_token")
                    addHeader(HttpHeaders.Origin, "http://invalid.origin")
                }.run {
                    response shouldHaveStatus HttpStatusCode(497, "")
                }
            }
            it("should succeed with valid csrf token") {
                val clientSession = ClientSession("")
                coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                coEvery {
                    sessionStorage.read("csrf_token")
                } returns "associatedClientRepresentation=#s${clientSession.token}"
                coEvery { sessionStorage.write(any(), any()) } just Runs
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader(HttpHeaders.Cookie, "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken, "csrf_token")
                    addHeader(HttpHeaders.Origin, "https://miyado.dev")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
            it("should succeed with valid csrf token and header of case insensitive") {
                val clientSession = ClientSession("")
                coEvery { sessionStorage.read("client") } returns "token=#s${clientSession.token}"
                coEvery {
                    sessionStorage.read("csrf_token")
                } returns "associatedClientRepresentation=#s${clientSession.token}"
                coEvery { sessionStorage.write(any(), any()) } just Runs
                delete(Paths.usersIdDelete.assignPathParams(1)) {
                    addHeader(HttpHeaders.Cookie, "client_session=client")
                    addHeader(CustomHeaders.XCSRFToken.lowercase(), "csrf_token")
                    addHeader(HttpHeaders.Origin, "https://miyado.dev")
                }.run {
                    response shouldHaveStatus HttpStatusCode.OK
                }
            }
        }
    }
}
