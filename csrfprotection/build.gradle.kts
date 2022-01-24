import com.github.hmiyado.Dependencies
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}

sourceSets {
    main {
    }
}

repositories {
    mavenCentral()
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = Dependencies.Kotlin.jvmTarget
    }
}
val compileTestKotlin = tasks.getByName("compileTestKotlin") {
    if (this is KotlinCompile) {
        kotlinOptions {
            jvmTarget = Dependencies.Kotlin.jvmTarget
        }
    }
}
val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    jvmArgs = listOf("-Dio.ktor.development=false")
    testLogging {
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events = setOf(TestLogEvent.SKIPPED, TestLogEvent.PASSED, TestLogEvent.FAILED)
    }
}

dependencies {
    implementation(Dependencies.Logback.classic)
    implementation(Dependencies.Ktor.sessions)
    testImplementation(Dependencies.Ktor.test)
    testImplementation(Dependencies.Mockk.core)
    testImplementation(Dependencies.Kotest.jUnit5)
    testImplementation(Dependencies.Kotest.ktor)
}
