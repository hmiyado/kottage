package com.github.hmiyado.authentication

import com.github.hmiyado.helper.AuthorizationHelper
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
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
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest

class AuthenticationConfigurationKtTest : DescribeSpec() {
    private lateinit var testApplicationEngine: TestApplicationEngine

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        testApplicationEngine = TestApplicationEngine()
        testApplicationEngine.start()
        with(testApplicationEngine) {
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
        }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        testApplicationEngine.stop(0L, 0L)
    }

    init {
        describe("admin") {
            it("should login as admin") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Get, "/") {
                        AuthorizationHelper.authorizeAsAdmin(this)
                    }) {
                        response shouldHaveStatus HttpStatusCode.OK
                    }
                }
            }

            it("should not login as admin") {
                with(testApplicationEngine) {
                    with(handleRequest(HttpMethod.Get, "/") {
                        // no authentication
                    }) {
                        response shouldHaveStatus HttpStatusCode.Unauthorized
                    }
                }
            }
        }
    }
}
