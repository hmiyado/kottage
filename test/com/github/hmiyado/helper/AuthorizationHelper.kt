package com.github.hmiyado.helper

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.server.testing.TestApplicationRequest
import java.util.*

class AuthorizationHelper {
    companion object {
        private const val adminName = "admin"
        private const val adminPassword = "admin"
        private val adminBase64: String = Base64.getEncoder().encodeToString("$adminName:$adminPassword".toByteArray())
        private val authenticationHeaderBasicAdmin = "Basic $adminBase64"

        fun installAuthentication(application: Application) {
            application.install(Authentication) {
                basic {
                    validate { (name, password) ->
                        if (name == adminName && password == adminName) {
                            return@validate UserIdPrincipal(adminName)
                        } else {
                            null
                        }
                    }
                }
            }
        }

        fun authorizeAsAdmin(request: TestApplicationRequest) {
            request.addHeader("Authorization", authenticationHeaderBasicAdmin)
        }
    }
}
