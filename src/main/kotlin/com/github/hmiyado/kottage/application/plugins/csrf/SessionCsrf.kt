package com.github.hmiyado.kottage.application.plugins.csrf

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlin.reflect.KClass

typealias CsrfOnFailFunction<T> = suspend ApplicationCall.(CsrfTokenSession<T>?) -> Unit

class SessionCsrfProvider<Client : Any> private constructor(
    config: Configuration<Client>
) : CsrfProvider(config) {
    val clientType: KClass<Client> = config.clientType

    val onFail: CsrfOnFailFunction<Client> = config.onFail

    class Configuration<Client : Any> constructor(
        val clientType: KClass<Client>,
    ) : CsrfProvider.Configuration() {
        var onFail: CsrfOnFailFunction<Client> = {}

        fun onFail(block: CsrfOnFailFunction<Client>) {
            onFail = block
        }

        fun buildProvider(): SessionCsrfProvider<Client> {
            return SessionCsrfProvider(this)
        }
    }
}

inline fun <reified Client : Any> Csrf.Configuration.session(
    configure: SessionCsrfProvider.Configuration<Client>.() -> Unit
) {
    val provider = SessionCsrfProvider
        .Configuration(Client::class)
        .apply(configure)
        .buildProvider()

    provider.pipeline.intercept(CsrfPipeline.CheckCsrfToken) { context ->
        val clientSession = call.sessions.get<Client>()
        val tokenSession = call.sessions.get<CsrfTokenSession<Client>>()

        if (clientSession == null) {
            provider.onFail(call, null)
            return@intercept
        }
        if (tokenSession?.associatedClient == clientSession) {
            context.isValid = true
            return@intercept
        }
        val newTokenSession = CsrfTokenSession(clientSession)
        call.sessions.clear<CsrfTokenSession<Client>>()
        call.sessions.set(newTokenSession)
        provider.onFail(call, newTokenSession)
    }

    register(provider)
}

open class CsrfTokenSession<Client : Any>(
    val associatedClient: Client
) {
    override fun toString(): String {
        return "CsrfTokenSession(associatedClient=$associatedClient)"
    }
}
