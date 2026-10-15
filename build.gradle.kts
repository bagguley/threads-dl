plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.versions)
}

group = "com.threadsdl"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose Multiplatform for Desktop
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.compose.components.resources)

    // HTTP client
    implementation(libs.okhttp)

    // JSON serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines for async operations
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    // Logging
    implementation(libs.slf4j.simple)

    // Selenium for headless browser automation
    implementation(libs.selenium.java)
    implementation(libs.selenium.chrome.driver)
}

compose.desktop {
    application {
        mainClass = "com.threadsdl.MainKt"
    }
}

compose.resources {
    packageOfResClass = "com.threadsdl.generated.resources"
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

// Configure the versions plugin
tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates").configure {
    // Reject release candidates and other non-stable versions
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
