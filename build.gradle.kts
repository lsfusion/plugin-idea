plugins {
    java
    // Must be compatible with Kotlin compiler bundled with IntelliJ IDEA 2025.3 (metadata 2.2.0)
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "com.lsfusion"
version = "1.0.304"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    // Compile with a stable JDK toolchain.
    // `runIde` in your environment was using javac 22.0.1 and crashed with StackOverflowError inside javac parser.
    // Pinning the toolchain makes builds reproducible on other IDE installs and on CI/Jenkins.
    toolchain {
        // IntelliJ IDEA 2025.3 platform jars are built with Java 21 bytecode (class version 65).
        // Therefore the compiler toolchain must be at least Java 21.
        // We still emit Java 17 bytecode via `sourceCompatibility/targetCompatibility` on the tasks below.
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

// IntelliJ Platform is configured via `dependencies { intellijPlatform { ... } }` in plugin 2.x.

sourceSets {
    main {
        java.srcDirs("src", "gen")
        kotlin.srcDirs("src", "gen")

        // Keep existing project layout: resources are spread across multiple roots.
        resources.srcDirs("resources", "src", "gen", "META-INF")
    }
}

// Ensure `META-INF/plugin.xml` is present in resources root for IntelliJ tasks (searchable options, sandbox).
// In this project `plugin.xml` is stored under `META-INF/` directory at the project root.
tasks.named<ProcessResources>("processResources") {
    from(layout.projectDirectory.file("META-INF/plugin.xml")) {
        into("META-INF")
    }
}

// `buildSearchableOptions` запускает headless IDE и падает на старых/кастомных code style providers.
// Для сборки/CI это не критично, поэтому отключаем, как рекомендовано сообщением Gradle задачи.
tasks.matching { it.name == "buildSearchableOptions" || it.name == "prepareJarSearchableOptions" }.configureEach {
    enabled = false
}

dependencies {
    // Old Ant build packaged everything from /lib except psiImplUtils.jar
    implementation(
        fileTree("lib") {
            include("*.jar")
            exclude("psiImplUtils.jar")
        }
    )

//    implementation(kotlin("reflect"))

    // Needed by MCP toolset DTOs; compiler plugin generates serializers.
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")

    // Annotations used in Java sources (e.g. `@NonNull`).
    compileOnly("org.jspecify:jspecify:1.0.0")

    // IntelliJ Platform + required bundled plugins.
    // NOTE: In IntelliJ Platform Gradle Plugin 2.x, the IntelliJ SDK is declared as a dependency.
    intellijPlatform {
        // Target IntelliJ IDEA (2025.3.x). This artifact provides the platform for compilation and `runIde`.
        // If you need to pin an exact patch build, you can set e.g. "2025.3.1.1".
        intellijIdea("2025.3")

        // Explicit bundled plugin deps so they are available without manual Project Structure setup.
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.properties")
        bundledPlugin("com.intellij.mcpServer")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
        )
    }
}

// NOTE:
// The legacy Ant build generated lexer/parser sources via JFlex + Grammar-Kit.
// The project already contains generated sources under `gen/` (and lexer outputs in `src/`).
// To keep Gradle build reliable without additional toolchain dependencies, we compile using
// existing generated sources. If you need regeneration tasks, we can add them via `JavaExec`
// (JFlex jar is present in the repo as `jflex-1.9.1.jar`) and a Grammar-Kit dependency.
