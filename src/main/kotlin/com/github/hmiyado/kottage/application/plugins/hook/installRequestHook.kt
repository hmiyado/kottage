package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.application.configuration.HookConfiguration
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.slf4j.LoggerFactory

fun Application.requestHook() {
    val client: HttpClient = get()
    val hookConfigurations: List<HookConfiguration> = get(named("HookConfigurations"))
    install(RequestHook) {
        logger = LoggerFactory.getLogger("Application")
        for (configuration in hookConfigurations) {
            hook(configuration.method, configuration.path) {
                client.post(configuration.requestTo) {}
            }
        }
    }
}
