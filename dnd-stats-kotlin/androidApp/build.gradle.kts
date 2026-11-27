plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.dndstats.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dndstats.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}

val composeVersion = "1.6.8"
val composeNavVersion = "2.7.7"
val lifecycleVersion = "2.8.4"
val activityComposeVersion = "1.9.1"

dependencies {
    implementation(project(":shared"))

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$composeNavVersion")

    // Lifecycle / ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // Activity
    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    // AppCompat (for Application subclass)
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("failed")
    }
}
