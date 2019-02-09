import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "4.0.4"
    application
    kotlin("jvm") version "1.3.20"
}

group = "kottage"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    testCompile("io.ktor:ktor-server-tests:$ktor_version")
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