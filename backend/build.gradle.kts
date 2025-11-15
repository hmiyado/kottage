import com.github.hmiyado.BuildConfigTemplate
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.openApi)
}

group = "kottage"
version = "v1"

val generatedSourcePath =
    layout.buildDirectory
        .dir("generated/src/main/kotlin")
        .get()
        .asFile
tasks.register("generateBuildConfig") {
    outputs.dir(generatedSourcePath)
    doLast {
        val template = BuildConfigTemplate.from(version.toString())
        val file =
            layout.buildDirectory
                .file("generated/version.txt")
                .get()
                .asFile
        file.appendText(template.version)
        template.writeKotlinFileTo(generatedSourcePath)
    }
}

application {
    if (System.getProperties().getProperty("cli") != null) {
        mainClass.set("com.github.hmiyado.kottage.cli.CliEntrypoint")
    } else {
        mainClass.set("io.ktor.server.netty.EngineMain")
    }
}

// https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
// https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin-server.md
openApiGenerate {
    generatorName.set("kotlin-server")
    inputSpec.set("$rootDir/src/main/resources/api-spec/root.json")
    val generatedDirPath =
        layout
            .buildDirectory
            .dir("generated")
            .get()
            .asFile
            .absolutePath
    outputDir.set(generatedDirPath)
    templateDir.set("$rootDir/src/main/resources/template")
    ignoreFileOverride.set("$rootDir/.openapi-generator-ignore")
    val rootPackage = "com.github.hmiyado.kottage.openapi"
    packageName.set(rootPackage)
    library.set("ktor")
    typeMappings.set(
        mutableMapOf(
            "DateTime" to "java.time.ZonedDateTime",
        ),
    )
    configOptions.put("enumPropertyNaming", "PascalCase")
    verbose.set(false)
}

sourceSets {
    main {
        java.srcDir(generatedSourcePath)
    }
}

repositories {
    mavenCentral()
}

val generateBuildConfig by tasks.getting(Task::class)
val openApiGenerate by tasks.getting(Task::class)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.kotlinJvmTarget.get()))
    }
    val javaVersion =
        when (libs.versions.kotlinJvmTarget.get()) {
            "21" -> JavaVersion.VERSION_21
            "17" -> JavaVersion.VERSION_17
            else -> JavaVersion.VERSION_1_8
        }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
kotlin {
    compilerOptions {
        val jvmVersion =
            when (libs.versions.kotlinJvmTarget.get()) {
                "21" -> JvmTarget.JVM_21
                "17" -> JvmTarget.JVM_17
                "11" -> JvmTarget.JVM_11
                else -> JvmTarget.JVM_1_8
            }
        jvmTarget.set(jvmVersion)
    }
}
val compileKotlin by tasks.getting(KotlinCompile::class) {
    dependsOn(generateBuildConfig, openApiGenerate)
}
tasks.getByName("compileTestKotlin") {
    dependsOn(generateBuildConfig, openApiGenerate)
}
tasks.getByName("runKtlintCheckOverMainSourceSet") {
    dependsOn(generateBuildConfig, openApiGenerate)
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
    implementation(libs.bundles.kottage)
    testImplementation(libs.bundles.testKottage)
}
