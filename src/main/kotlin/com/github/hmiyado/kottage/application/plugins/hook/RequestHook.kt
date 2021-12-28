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
import org.slf4j.Logger

class RequestHook(configuration: Configuration) {
    private val logger = configuration.logger
    private val hooks: List<Hook> = configuration.hooks.toList()

    private suspend fun ApplicationCall.runHook(method: HttpMethod, path: String) {
        hooks
            .filter { hook -> hook.filter(method, path) }
            .forEach { hook ->
                try {
                    hook.runner(this)
                } catch (e: Throwable) {
                    logger?.error(e.message)
                }
            }
    }

    suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        val call = context.call
        val method = call.request.httpMethod
        val path = call.request.path()

        call.runHook(method, path)
    }

    class Configuration {
        var logger: Logger? = null
        val hooks: MutableList<Hook> = mutableListOf()

        fun hook(method: HttpMethod, path: String, runner: suspend ApplicationCall.() -> Unit) {
            hooks.add(Hook(HookFilter.exactMatch(method, path), runner))
        }

        fun hook(filter: HookFilter, runner: suspend ApplicationCall.() -> Unit) {
            hooks.add(Hook(filter, runner))
        }
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
