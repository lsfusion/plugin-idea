// Enable Gradle JVM toolchain auto-provisioning (needed when JDK 17 isn't installed locally).
// Version 1.0.0 is required for Gradle 9.x because older releases still reference
// the removed `JvmVendorSpec.IBM_SEMERU` constant.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lsfusion-idea-plugin"
