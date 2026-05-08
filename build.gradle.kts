plugins {
    kotlin("multiplatform") version "2.3.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    wasmWasi {
        nodejs()
        binaries.executable()
    }
}

tasks.register("runWasm") {
    dependsOn("wasmWasiNodeRun")
}
