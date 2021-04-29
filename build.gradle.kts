import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val logback_version: String by project
val exposed_version: String by project
val postgresql_version: String by project
val graphql_java_version: String by project
val koin_version: String by project

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
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("com.graphql-java:graphql-java:$graphql_java_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.2.1")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")
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