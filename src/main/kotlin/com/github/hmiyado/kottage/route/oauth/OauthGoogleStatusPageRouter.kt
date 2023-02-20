package com.github.hmiyado.kottage.route.oauth

import com.github.hmiyado.kottage.application.plugins.statuspages.ErrorFactory
import com.github.hmiyado.kottage.repository.users.UserRepository
import com.github.hmiyado.kottage.route.StatusPageRouter
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

object OauthGoogleStatusPageRouter : StatusPageRouter {
    override fun addStatusPage(configuration: StatusPagesConfig) {
        with(configuration) {
            exception<UserRepository.ConflictOidcTokenException> { call: ApplicationCall, _ ->
                call.respond(HttpStatusCode.Conflict, ErrorFactory.create409())
            }
        }
    }
}
