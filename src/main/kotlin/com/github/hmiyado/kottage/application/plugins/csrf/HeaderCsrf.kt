package com.github.hmiyado.kottage.application.plugins.csrf

import io.ktor.application.ApplicationCall
import io.ktor.application.call

private typealias CsrfHeaderValidatorFunction = (header: String, values: List<String>) -> Boolean
private typealias HeaderCsrfOnFailFunction = suspend ApplicationCall.() -> Unit


class HeaderCsrfProvider private constructor(
    config: Configuration
) : CsrfProvider(config) {
    val onFail: HeaderCsrfOnFailFunction = config.onFail
    val validator: CsrfHeaderValidatorFunction = config.validator

    class Configuration : CsrfProvider.Configuration() {
        var onFail: HeaderCsrfOnFailFunction = {}

        var validator: CsrfHeaderValidatorFunction = { _, _ -> false }

        fun validator(validator: CsrfHeaderValidatorFunction) {
            this.validator = validator
        }

        fun onFail(block: HeaderCsrfOnFailFunction) {
            onFail = block
        }

        fun buildProvider(): HeaderCsrfProvider {
            return HeaderCsrfProvider(this)
        }
    }
}

inline fun Csrf.Configuration.header(
    configure: HeaderCsrfProvider.Configuration.() -> Unit
) {
    val provider = HeaderCsrfProvider
        .Configuration()
        .apply(configure)
        .buildProvider()

    provider.pipeline.intercept(CsrfPipeline.CheckCsrfToken) { context ->
        val isValid = call.request.headers.entries().any { (key, values) -> provider.validator(key, values) }
        if (!isValid) {
            context.isValid = false
            provider.onFail(call)
        }
    }

    register(provider)
}
