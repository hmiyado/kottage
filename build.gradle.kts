import com.github.hmiyado.Dependencies
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val postgresql_version: String by project
val graphql_java_version: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "4.0.4"
    application
    kotlin("jvm") version "1.4.32"
}

group = "kottage"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
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
    implementation(Dependencies.Ktor.gson)
    implementation(Dependencies.Logback.classic)
    implementation(Dependencies.Exposed.core)
    implementation(Dependencies.Exposed.dao)
    implementation(Dependencies.Exposed.jdbc)
    implementation(Dependencies.Exposed.javaTime)
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("com.graphql-java:graphql-java:$graphql_java_version")
    implementation(Dependencies.Koin.ktor)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.2.1")
    testImplementation(Dependencies.Ktor.test)
    testImplementation(Dependencies.Koin.test)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

val shadowJar: ShadowJar by tasks
// This task will generate your fat JAR and put it in the ./build/libs/ directory
shadowJar.apply {
    baseName = project.group.toString()
    classifier = ""
    version = ""
    manifest.apply {
        attributes["Main-Class"] = application.mainClassName
    }
}