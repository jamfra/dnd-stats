plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("com.lemonappdev:konsist:0.17.3")
            implementation("io.mockk:mockk:1.13.13")
            implementation("org.junit.jupiter:junit-jupiter:5.11.3")
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }
}

android {
    namespace = "com.dndstats.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()

    testLogging {
        events("failed")
    }

    afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (desc.parent == null) {
            println("\nResults (:shared:jvmTest): ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)")
        }
    }))
}

tasks.matching { it.name == "test" }.configureEach {
    dependsOn("allTests")
}
