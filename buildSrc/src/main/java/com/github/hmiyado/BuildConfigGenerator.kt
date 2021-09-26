package com.github.hmiyado

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

object BuildConfigGenerator {
    fun generate(destination: File, version: String? = null) {
        val className = "BuildConfig"
        val file = FileSpec.builder("com.github.hmiyado.application.build", className)
            .addType(
                TypeSpec.objectBuilder(className)
                    .addProperty(
                        PropertySpec.builder("version", String::class, KModifier.CONST)
                            .initializer((version ?: "0.0.0").wrappedRawLiteral())
                            .build()
                    )
                    .build()
            )
            .build()
        file.writeTo(destination)
    }

    private fun String.wrappedRawLiteral(): String {
        return "\"${this}\""
    }
}
