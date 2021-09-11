package com.github.hmiyado.kottage.authentication

import com.github.hmiyado.kottage.helper.AuthorizationHelper
import com.github.hmiyado.kottage.helper.KtorApplicationTestListener
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DescribeSpec
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

class AuthenticationConfigurationKtTest : DescribeSpec() {
    private val ktorListener = KtorApplicationTestListener(beforeSpec = {
        with(application) {
            install(Authentication) {
                admin(AuthorizationHelper.adminCredential)
            }
            routing {
                authenticate {
                    get("/") {
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
                ktorListener.handleJsonRequest(HttpMethod.Get, "/") {
                    AuthorizationHelper.authorizeAsAdmin(this)
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
