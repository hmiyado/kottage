package com.github.hmiyado.kottage.application.plugins.hook

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.PipelinePhase

data class Hook(
    val filter: HookFilter,
    val runner: suspend ApplicationCall.() -> Unit,
)

abstract class HookFilter(
    val pipelinePhase: PipelinePhase,
    /**
     * if [insertAfter] is true, this hook is inserted after [pipelinePhase].
     * if [insertAfter] is false, this hook is inserted before [pipelinePhase]
     */
    val insertAfter: Boolean = true,
) : (HttpMethod, String) -> Boolean {
    companion object {
        fun exactMatch(
            method: HttpMethod, path: String,
            pipelinePhase: PipelinePhase = ApplicationCallPipeline.Call,
            insertAfter: Boolean = true,
        ) =
            object : HookFilter(pipelinePhase, insertAfter) {
                override fun invoke(p1: HttpMethod, p2: String): Boolean {
                    return p1 == method && p2 == path
                }
            }

        fun match(
            pipelinePhase: PipelinePhase = ApplicationCallPipeline.Call,
            insertAfter: Boolean = true,
            block: (HttpMethod, String) -> Boolean = { _, _ -> false },
        ) = object : HookFilter(pipelinePhase, insertAfter) {
            override fun invoke(p1: HttpMethod, p2: String): Boolean {
                return block(p1, p2)
            }
        }
    }
}
