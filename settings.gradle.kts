// Enable Gradle JVM toolchain auto-provisioning (needed when JDK 17 isn't installed locally).
// This makes builds reproducible on other machines and on CI.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "lsfusion-idea-plugin"
