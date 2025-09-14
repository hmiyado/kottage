package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.slf4j.LoggerFactory

fun Application.requestHook() {
    install(RequestHook) {
        logger = LoggerFactory.getLogger("Application")
        outgoingWebhook(get(), get(named("HookConfigurations")))
    }
}

private fun RequestHook.Configuration.outgoingWebhook(
    client: HttpClient,
    hookConfigurations: List<HookConfiguration>,
) {
    for (configuration in hookConfigurations) {
        hook(configuration.method, configuration.path) {
            client.post(configuration.requestTo) {}
        }
    }
}
