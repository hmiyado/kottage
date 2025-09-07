import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.testKarate)
    testImplementation(libs.testJunit)
    testRuntimeOnly(libs.testJunitEngine)
    testRuntimeOnly(libs.testJunitLauncher)
}

sourceSets["test"].resources {
    srcDir(file("src/test/kotlin"))
    exclude("**/*.kt")
}
java {
    val javaVersion = when(libs.versions.kotlinJvmTarget.get()) {
        "17" -> JavaVersion.VERSION_17
        else -> JavaVersion.VERSION_1_8
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    systemProperty("karate.options", System.getProperty("karate.options"))
    systemProperty("karate.env", System.getProperty("karate.env"))
    outputs.upToDateWhen { false }
}

tasks.register<JavaExec>("karateDebug") {
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.intuit.karate.cli.Main")
}
