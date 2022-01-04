package com.github.hmiyado.kottage.application

import com.github.hmiyado.kottage.application.configuration.DevelopmentConfiguration
import com.github.hmiyado.kottage.application.plugins.CustomHeaders
import com.github.hmiyado.kottage.application.plugins.authentication.admin
import com.github.hmiyado.kottage.application.plugins.authentication.users
import com.github.hmiyado.kottage.application.plugins.csrf.csrf
import com.github.hmiyado.kottage.application.plugins.defaultHeaders
import com.github.hmiyado.kottage.application.plugins.hook.requestHook
import com.github.hmiyado.kottage.application.plugins.initializeKoinModules
import com.github.hmiyado.kottage.application.plugins.sessions
import com.github.hmiyado.kottage.repository.initializeDatabase
import com.github.hmiyado.kottage.route.routing
import com.github.hmiyado.kottage.route.statusPages
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get

fun Application.main() {
    install(Koin) {
        initializeKoinModules(this@main.environment)
    }
    initializeDatabase(get())
    install(CallLogging)
    defaultHeaders()
    install(CORS) {
        allowCredentials = true
        when (get<DevelopmentConfiguration>()) {
            DevelopmentConfiguration.Development -> host("localhost:3000")
            DevelopmentConfiguration.Production -> {
                host("miyado.dev", schemes = listOf("https"), subDomains = listOf("www"))
            }
        }
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Patch)
        method(HttpMethod.Delete)
        method(HttpMethod.Post)
        header(HttpHeaders.ContentType)
        header(CustomHeaders.XCSRFToken)
        exposeHeader(CustomHeaders.XCSRFToken)
    }
    install(AutoHeadResponse)
    statusPages()
    contentNegotiation()
    install(Authentication) {
        admin(get(), get(), get())
        users(get())
    }
    sessions()
    csrf()
    routing()
    requestHook()
}
