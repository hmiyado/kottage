import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.testKarate)
}

sourceSets["test"].resources {
    srcDir(file("src/test/kotlin"))
    exclude("**/*.kt")
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
