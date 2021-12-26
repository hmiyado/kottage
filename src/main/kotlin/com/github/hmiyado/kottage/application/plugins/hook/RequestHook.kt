package com.github.hmiyado.kottage.application.plugins.hook

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelinePhase

class RequestHook(configuration: Configuration) {
    private val hooks: List<Configuration.Hook> = configuration.hooks.toList()

    private suspend fun runHook(method: HttpMethod, path: String) {
        hooks
            .filter { hook -> hook.method == method && hook.path == path }
            .forEach { hook ->
                hook.runner()
            }
    }

    suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        val call = context.call
        val method = call.request.httpMethod
        val path = call.request.path()

        runHook(method, path)
    }

    class Configuration {
        val hooks: MutableList<Hook> = mutableListOf()

        fun hook(method: HttpMethod, path: String, runner: suspend () -> Unit) {
            hooks.add(Hook(method, path, runner))
        }

        data class Hook(
            val method: HttpMethod,
            val path: String,
            val runner: suspend () -> Unit,
        )
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RequestHook> {
        override val key: AttributeKey<RequestHook>
            get() = AttributeKey("RequestHook")

        private val phaseOnCallSuccess = PipelinePhase("OnCallSuccess")
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): RequestHook {
            val configuration = Configuration().apply(configure)
            val feature = RequestHook(configuration)

            pipeline.insertPhaseAfter(ApplicationCallPipeline.Call, phaseOnCallSuccess)
            pipeline.intercept(phaseOnCallSuccess) {
                feature.intercept(this)
            }

            return feature
        }
    }
}
