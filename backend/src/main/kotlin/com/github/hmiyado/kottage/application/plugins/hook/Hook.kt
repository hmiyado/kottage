package com.github.hmiyado.kottage.application.plugins.hook

import com.github.hmiyado.kottage.route.matchesConcretePath
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
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
            method: HttpMethod,
            path: String,
            pipelinePhase: PipelinePhase = ApplicationCallPipeline.Call,
            insertAfter: Boolean = true,
        ) = object : HookFilter(pipelinePhase, insertAfter) {
            override fun invoke(
                p1: HttpMethod,
                p2: String,
            ): Boolean = p1 == method && path.matchesConcretePath(p2)
        }

        fun match(
            pipelinePhase: PipelinePhase = ApplicationCallPipeline.Call,
            insertAfter: Boolean = true,
            block: (HttpMethod, String) -> Boolean = { _, _ -> false },
        ) = object : HookFilter(pipelinePhase, insertAfter) {
            override fun invoke(
                p1: HttpMethod,
                p2: String,
            ): Boolean = block(p1, p2)
        }
    }
}
