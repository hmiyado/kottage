import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

// mise.toml (リポジトリルート) を Java バージョンの Single Source of Truth とする。
// buildSrc は自分自身が MiseVersions のコンパイル対象なので、buildSrc/src の
// MiseVersions クラスをここで使うことはできない (循環依存になる)。そのため
// このファイルだけ簡易的なパース処理を直接持つ。
val miseJavaVersion =
    Regex("""^\s*java\s*=\s*"temurin-(\d+)""", RegexOption.MULTILINE)
        .find(File(rootDir, "../../mise.toml").readText())
        ?.groupValues
        ?.get(1)
        ?: error("Could not find java version in mise.toml")

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

dependencies {
    implementation(libs.bundles.buildSrc)
}
