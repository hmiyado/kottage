plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
}
