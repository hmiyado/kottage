plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    val karateVersion = "1.0.0"
    testImplementation("com.intuit.karate:karate-junit5:${karateVersion}")
}

sourceSets["test"].resources {
    srcDir(file("src/test/kotlin"))
    exclude("**/*.kt")
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
