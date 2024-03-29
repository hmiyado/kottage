package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import com.github.hmiyado.kottage.application.plugins.csrf.ClientSession
import com.github.hmiyado.kottage.service.users.RandomGenerator
import io.github.hmiyado.ktor.csrfprotection.Csrf
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.slf4j.LoggerFactory

fun Application.requestHook() {
    install(RequestHook) {
        logger = LoggerFactory.getLogger("Application")
        outgoingWebhook(get(), get(named("HookConfigurations")))
        insertClientSession(get())
    }
}

private fun RequestHook.Configuration.outgoingWebhook(client: HttpClient, hookConfigurations: List<HookConfiguration>) {
    for (configuration in hookConfigurations) {
        hook(configuration.method, configuration.path) {
            client.post(configuration.requestTo) {}
        }
    }
}

private fun RequestHook.Configuration.insertClientSession(randomGenerator: RandomGenerator) {
    hook(
        HookFilter.match(Csrf.CsrfPhase, insertAfter = false) { _, _ ->
            true
        },
    ) {
        val clientSession = sessions.get<ClientSession>()
        if (clientSession == null) {
            sessions.set(ClientSession(randomGenerator.generateString()))
        }
    }
}
