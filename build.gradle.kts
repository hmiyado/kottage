import com.github.hmiyado.BuildConfigGenerator
import com.github.hmiyado.Dependencies
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    val kotlinVersion = "1.5.31"
    application
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "kottage"
version = "0.0.1"

val generatedSourcePath = buildDir.resolve(File("generated/source"))
BuildConfigGenerator.generate(destination = generatedSourcePath, version = version.toString())

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

sourceSets {
    main {
        java.srcDir(generatedSourcePath)
    }
}

repositories {
    mavenCentral()
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
    implementation(Dependencies.Ktor.serverNetty)
    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Ktor.auth)
    implementation(Dependencies.Ktor.locations)
    implementation(Dependencies.Ktor.sessions)
    implementation(Dependencies.Logback.classic)
    implementation(Dependencies.Exposed.core)
    implementation(Dependencies.Exposed.dao)
    implementation(Dependencies.Exposed.jdbc)
    implementation(Dependencies.Exposed.javaTime)
    implementation(Dependencies.PostgreSql.core)
    implementation(Dependencies.MySql.core)
    implementation(Dependencies.Koin.ktor)
    testImplementation(Dependencies.JUnit.jupiter)
    testImplementation(Dependencies.Kotest.jUnit5)
    testImplementation(Dependencies.Kotest.json)
    testImplementation(Dependencies.Kotest.koin)
    testImplementation(Dependencies.Kotest.ktor)
    testImplementation(Dependencies.Ktor.test)
    testImplementation(Dependencies.Koin.test)
    testImplementation(Dependencies.Mockk.core)
}
