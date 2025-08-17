package com.github.hmiyado.kottage.application.plugins.hook

import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import org.slf4j.Logger

class RequestHook(configuration: Configuration) {
    private val logger = configuration.logger
    private val hooks: List<Hook> = configuration.hooks.toList()

    fun intercept(pipeline: ApplicationCallPipeline) {
        hooks
            .groupBy { hook -> hook.filter.pipelinePhase to hook.filter.insertAfter }
            .forEach { (group, hooksByPhase) ->
                val (phase, insertAfter) = group

                val hookPhase: PipelinePhase = if (insertAfter) {
                    PipelinePhase("RequestHookAfter${phase.name}").also {
                        pipeline.insertPhaseAfter(phase, it)
                    }
                } else {
                    PipelinePhase("RequestHookBefore${phase.name}").also {
                        pipeline.insertPhaseBefore(phase, it)
                    }
                }
                pipeline.intercept(hookPhase) {
                    val call = call
                    val method = call.request.httpMethod
                    val path = call.request.path()
                    hooksByPhase
                        .filter { hook -> hook.filter(method, path) }
                        .forEach { hook ->
                            try {
                                hook.runner(call)
                            } catch (e: Throwable) {
                                logger?.error(e.message)
                            }
                        }
                }
            }
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

    companion object Feature : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, RequestHook> {
        override val key: AttributeKey<RequestHook>
            get() = AttributeKey("RequestHook")

        private val phaseOnCallSuccess = PipelinePhase("OnCallSuccess")
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): RequestHook {
            val configuration = Configuration().apply(configure)
            val feature = RequestHook(configuration)

            feature.intercept(pipeline)

            return feature
        }
    }
}
