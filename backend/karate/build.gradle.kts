import com.github.hmiyado.MiseVersions
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
// mise.toml (リポジトリルート) を Java バージョンの Single Source of Truth とする
val miseJavaVersion = MiseVersions.readJavaMajorVersion(File(rootDir, "../mise.toml"))
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(miseJavaVersion))
    }
    sourceCompatibility = JavaVersion.toVersion(miseJavaVersion)
    targetCompatibility = JavaVersion.toVersion(miseJavaVersion)
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(miseJavaVersion))
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
