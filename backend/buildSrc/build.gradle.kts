import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.kotlinJvmTarget.get()))
    }
    val javaVersion =
        when (libs.versions.kotlinJvmTarget.get()) {
            "21" -> JavaVersion.VERSION_21
            "17" -> JavaVersion.VERSION_17
            else -> JavaVersion.VERSION_1_8
        }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
kotlin {
    compilerOptions {
        val jvmVersion = when (libs.versions.kotlinJvmTarget.get()) {
            "21" -> JvmTarget.JVM_21
            "17" -> JvmTarget.JVM_17
            "11" -> JvmTarget.JVM_11
            else -> JvmTarget.JVM_1_8
        }
        jvmTarget.set(jvmVersion)
    }
}

dependencies {
    implementation(libs.bundles.buildSrc)
}
