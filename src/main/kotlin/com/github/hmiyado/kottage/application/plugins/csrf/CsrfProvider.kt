package com.github.hmiyado.kottage.application.plugins.csrf


open class CsrfProvider(
    configuration: Configuration
) {
    val pipeline: CsrfPipeline = CsrfPipeline(developmentMode = configuration.pipeline.developmentMode)


    open class Configuration {
        val pipeline: CsrfPipeline = CsrfPipeline(developmentMode = false)
    }
}
