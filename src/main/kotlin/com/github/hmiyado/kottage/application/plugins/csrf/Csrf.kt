package com.github.hmiyado.kottage.application.plugins.csrf

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase

class Csrf(configuration: Configuration) {
    private val provider: CsrfProvider = requireNotNull(configuration.provider)

    fun intercept(
        pipeline: ApplicationCallPipeline,
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, CsrfPhase)

        pipeline.intercept(CsrfPhase) {
            val context = CsrfContext(call)
            provider.pipeline.execute(call, context)
        }
    }

    class Configuration {
        var provider: CsrfProvider? = null

        fun register(provider: CsrfProvider) {
            this.provider = provider
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Csrf> {
        val CsrfPhase = PipelinePhase("Csrf")

        override val key: AttributeKey<Csrf>
            get() = AttributeKey("Csrf")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Csrf {
            val configuration = Configuration().apply(configure)
            val feature = Csrf(configuration)

            feature.intercept(pipeline)

            return feature
        }

    }
}
