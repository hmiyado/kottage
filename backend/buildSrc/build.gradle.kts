import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        val jvmVersion = when (libs.versions.kotlinJvmTarget.get()) {
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
