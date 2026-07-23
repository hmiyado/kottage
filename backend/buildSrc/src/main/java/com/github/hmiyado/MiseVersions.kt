package com.github.hmiyado

import java.io.File

/**
 * mise.toml (リポジトリルート) を Java バージョンの Single Source of Truth として扱うためのヘルパー。
 * Docker イメージ・CI・Gradle のツールチェーンで同じバージョンを参照するために使う。
 */
object MiseVersions {
    private val javaVersionRegex = Regex("""^\s*java\s*=\s*"temurin-(\d+)""", RegexOption.MULTILINE)

    fun readJavaMajorVersion(miseToml: File): String {
        val text = miseToml.readText()
        val match =
            javaVersionRegex.find(text)
                ?: error("Could not find java version in ${miseToml.path}")
        return match.groupValues[1]
    }
}
