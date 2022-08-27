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

val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = libs.versions.kotlinJvmTarget.get()
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
    main = "com.intuit.karate.cli.Main"
}
