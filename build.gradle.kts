@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    kotlin("multiplatform") version "2.3.20"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    wasmWasi {
        binaries.executable()
        nodejs()
    }
}

tasks.register("runWasm") {
    group = "application"
    description = "Builds and runs the Wasm/WASI executable with Node.js."
    dependsOn("wasmWasiNodeRun")
}

tasks.register("wasmWasiNodeRun") {
    group = "application"
    description = "Runs the Wasm/WASI executable with Node.js."
    dependsOn("wasmWasiNodeDevelopmentRun")
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xwasm-use-new-exception-proposal=false")
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec>().configureEach {
    standardInput = System.`in`
}
