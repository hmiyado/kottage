package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.authentication.admin
import com.github.hmiyado.kottage.application.plugins.authentication.oidcGoogle
import com.github.hmiyado.kottage.application.plugins.authentication.users
import com.github.hmiyado.kottage.application.plugins.csrf.csrf
import com.github.hmiyado.kottage.application.plugins.defaultHeaders
import com.github.hmiyado.kottage.application.plugins.hook.requestHook
import com.github.hmiyado.kottage.application.plugins.initializeKoinModules
import com.github.hmiyado.kottage.application.plugins.sessions
import com.github.hmiyado.kottage.application.plugins.statuspages.statusPages
import com.github.hmiyado.kottage.repository.initializeDatabase
import com.github.hmiyado.kottage.route.routing
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun Application.main() {
    install(Koin) {
        initializeKoinModules(this@main.environment)
    }
    initializeDatabase(get())
    install(CallLogging)
    defaultHeaders()
    install(CORS) {
        allowCredentials = true
        when (this@main.get<DevelopmentConfiguration>()) {
            DevelopmentConfiguration.Development -> allowHost("localhost:3000")
            DevelopmentConfiguration.Production -> {
                allowHost("miyado.dev", schemes = listOf("https"), subDomains = listOf("www"))
            }
        }
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(CustomHeaders.XCSRFToken)
        exposeHeader(CustomHeaders.XCSRFToken)
    }
    install(AutoHeadResponse)
    statusPages()
    contentNegotiation()
    install(Authentication) {
        admin(this@main.get(), this@main.get(), this@main.get())
        users(this@main.get())
        oidcGoogle(this@main.get(), this@main.get(), this@main.get(), this@main.get(), this@main.get(named("pre-oauth-states")))
    }
    sessions()
    csrf()
    routing()
    requestHook()
}
