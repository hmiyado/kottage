package com.github.hmiyado

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BuildConfigTemplate(
    val versionPrefix: String,
    val buildDateTime: String,
    val commitHash: String,
) {
    val version: String = "$versionPrefix-$buildDateTime"
    val versionWithCommitHash: String = "$versionPrefix-$buildDateTime+$commitHash"

    fun writeKotlinFileTo(destination: File) {
        val className = "BuildConfig"
        val file = FileSpec.builder("com.github.hmiyado.application.build", className)
            .addType(
                TypeSpec.objectBuilder(className)
                    .addProperty(
                        PropertySpec.builder("version", String::class, KModifier.CONST)
                            .initializer(versionWithCommitHash.wrappedRawLiteral())
                            .build()
                    )
                    .build()
            )
            .build()
        file.writeTo(destination)
    }

    companion object {
        fun from(versionPrefix: String?): BuildConfigTemplate {
            val dateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmm"))
            return BuildConfigTemplate(
                versionPrefix = versionPrefix ?: "0.0.0",
                buildDateTime = dateTimeString,
                commitHash = BuildConfigTemplate.getGitHash(),
            )
        }

        private fun String.wrappedRawLiteral(): String {
            return "\"${this}\""
        }

        private fun getGitHash(): String {
            val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start()
            process.waitFor()

            return try {
                val br = BufferedReader(InputStreamReader(process.inputStream))
                br.readLine()
            } catch (e: Throwable) {
                "nogithash"
            }
        }
    }
}
