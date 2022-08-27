plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = libs.versions.kotlinJvmTarget.get()
    }
}

dependencies {
    implementation(libs.bundles.buildSrc)
}
