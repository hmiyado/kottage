import com.github.hmiyado.Dependencies

plugins {
    application
    kotlin("jvm") version "1.4.32"
}

group = "kottage"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Dependencies.Ktor.serverNetty)
    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Logback.classic)
    implementation(Dependencies.Exposed.core)
    implementation(Dependencies.Exposed.dao)
    implementation(Dependencies.Exposed.jdbc)
    implementation(Dependencies.Exposed.javaTime)
    implementation(Dependencies.PostgreSql.core)
    implementation(Dependencies.Koin.ktor)
    testImplementation(Dependencies.JUnit.jupiter)
    testImplementation(Dependencies.KotlinTest.jUnit5)
    testImplementation(Dependencies.Ktor.test)
    testImplementation(Dependencies.Koin.test)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
