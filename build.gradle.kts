plugins {
    java
    // Must be compatible with Kotlin compiler bundled with IntelliJ IDEA 2025.3 (metadata 2.2.0)
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

val ideaVersion = "2025.3"
val javaVersion = 21

group = "com.lsfusion"
version = file("META-INF/plugin.xml").let {
    if (it.exists()) {
        val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it)
        doc.getElementsByTagName("version").item(0).textContent
    } else {
        "1.0.0"
    }
}

repositories {
    mavenCentral()
    maven("https://plugins.jetbrains.com/maven")
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
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

kotlin {
    jvmToolchain(javaVersion)
}

// IntelliJ Platform is configured via `dependencies { intellijPlatform { ... } }` in plugin 2.x.

sourceSets {
    main {
        java.srcDirs("src", "gen")
        kotlin.srcDirs("src", "gen")

        // Keep existing project layout: resources are spread across multiple roots.
        resources.srcDirs("resources")
    }
}

// Ensure `META-INF/` metadata (plugin.xml, mcp.xml) is present in the final jar.
tasks.named<ProcessResources>("processResources") {
    from(layout.projectDirectory.dir("META-INF")) {
        into("META-INF")
    }
}

val packPsiImplUtils by tasks.registering(Jar::class) {
    description = "Pack psiImplUtils jar"
    archiveFileName.set("psiImplUtils.jar")
    destinationDirectory.set(layout.projectDirectory.dir("buildLib"))

    val compileJava = tasks.named<JavaCompile>("compileJava")
    val compileKotlin = tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin")

    from(compileJava.map { it.destinationDirectory }) {
        include("com/lsfusion/migration/lang/psi/MigrationPsiImplUtil.class")
        include("com/lsfusion/lang/psi/LSFPsiImplUtil.class")
        include("com/lsfusion/lang/psi/LSFPsiImplUtil$*.class")
    }
    from(compileKotlin.map { it.destinationDirectory }) {
        include("com/lsfusion/migration/lang/psi/MigrationPsiImplUtil.class")
        include("com/lsfusion/lang/psi/LSFPsiImplUtil.class")
        include("com/lsfusion/lang/psi/LSFPsiImplUtil$*.class")
    }

    dependsOn(compileJava, compileKotlin)
}

// `buildSearchableOptions` запускает headless IDE и падает на старых/кастомных code style providers.
// Для сборки/CI это не критично, поэтому отключаем, как рекомендовано сообщением Gradle задачи.
tasks.matching {
    it.name == "buildSearchableOptions"
            || it.name == "prepareJarSearchableOptions"
            || it.name == "jarSearchableOptions"
}.configureEach {
    enabled = false
}

// Configurations for lexer and parser generation
val jflexConfig by configurations.creating
val grammarKitConfig by configurations.creating

dependencies {
    jflexConfig(files("buildLib/jflex.jar"))
    grammarKitConfig(files("buildLib/grammar-kit.jar"))

    // Libraries previously in lib/
    implementation("commons-beanutils:commons-beanutils:1.8.0")
    implementation("org.apache.commons:commons-lang3:3.4")
    implementation("jgraph:jgraph:5.13.0.0")
    implementation("org.jgrapht:jgrapht-core:0.9.0")
    implementation("org.jgrapht:jgrapht-ext:0.9.0")
    implementation("org.json:json:20180813")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("net.gcardone.junidecode:junidecode:0.5.2")
    implementation("com.microsoft.onnxruntime:onnxruntime:1.23.2")
    implementation("ai.djl.huggingface:tokenizers:0.36.0")
    implementation("org.apache.lucene:lucene-core:10.3.2")
    implementation("org.apache.lucene:lucene-analysis-common:10.3.2")

    // Needed by MCP toolset DTOs; compiler plugin generates serializers.
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")

    // Annotations used in Java sources (e.g. `@NonNull`).
    compileOnly("org.jspecify:jspecify:1.0.0")

    // IntelliJ Platform + required bundled plugins.
    // NOTE: In IntelliJ Platform Gradle Plugin 2.x, the IntelliJ SDK is declared as a dependency.
    intellijPlatform {
        // Target IntelliJ IDEA. This artifact provides the platform for compilation.
        intellijIdea(ideaVersion)

        // Explicit bundled plugin deps so they are available without manual Project Structure setup.
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.properties")
        bundledPlugin("com.intellij.mcpServer")
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }
    publishing {
        token.set(providers.gradleProperty("intellijPublishToken"))
    }
}

tasks.publishPlugin {
    dependsOn(tasks.verifyPlugin)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(javaVersion.toString()))
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
        )
    }
}

abstract class LexerTask @javax.inject.Inject constructor(
    private val fileSystemOperations: org.gradle.api.file.FileSystemOperations
) : JavaExec() {
    @get:InputFile
    abstract val flexFile: RegularFileProperty

    @get:InputFile
    abstract val skeletonFile: RegularFileProperty

    @get:Internal
    abstract val outputDir: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        mainClass.set("jflex.Main")
    }

    override fun exec() {
        args(
            "--skel", skeletonFile.get().asFile.absolutePath,
            "--nobak",
            flexFile.get().asFile.absolutePath,
            "-d", outputDir.get().asFile.absolutePath
        )
        super.exec()
    }
}

@CacheableTask
abstract class ParserTask @Inject constructor(
    private val objects: ObjectFactory,
    private val layout: ProjectLayout,
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val bnfFile: RegularFileProperty

    @get:Internal
    abstract val outputDirs: ListProperty<String>

    /**
     * IntelliJ SDK jars are required only to RUN the generator,
     * but must NOT be a Gradle task input (otherwise Gradle fingerprints hundreds of jars).
     */
    @get:Internal
    abstract val intellijPlatformJars: ConfigurableFileCollection

    /**
     * Precomputed IDEA lib directory (…/lib). Stored as Internal to avoid fingerprinting.
     * Also avoids resolving IntelliJ configuration during task execution (configuration cache friendly).
     */
    @get:Internal
    abstract val ideaLibDir: DirectoryProperty

    /** Small classpath inputs that really affect generation. */
    @get:Classpath
    abstract val grammarKitClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val psiImplUtilsJar: ConfigurableFileCollection

    /** Declare output directories so Gradle can track UP-TO-DATE and cache state. */
    @get:OutputDirectories
    val outputDirectories: List<Directory>
        get() = outputDirs.get().map { layout.projectDirectory.dir(it) }

    @TaskAction
    fun generate() {
        val ideaLibTree = objects.fileTree().setDir(ideaLibDir.get()).matching { include("*.jar") }

        execOperations.javaexec {
            mainClass.set("org.intellij.grammar.Main")
            classpath = grammarKitClasspath + psiImplUtilsJar + ideaLibTree
            args("gen", bnfFile.get().asFile.absolutePath)
        }
    }
}

fun TaskContainer.registerLexerTask(name: String, flexPath: String, outputDirPath: String, outputPath: String) =
    register<LexerTask>(name) {
        classpath = jflexConfig
        flexFile.set(layout.projectDirectory.file(flexPath))
        skeletonFile.set(layout.projectDirectory.file("buildLib/idea-flex.skeleton"))
        outputDir.set(layout.projectDirectory.dir(outputDirPath))
        outputFile.set(layout.projectDirectory.file(outputPath))
    }

fun TaskContainer.registerParserTask(name: String, bnfPath: String, outputDirPaths: List<String>) =
    register<ParserTask>(name) {
        val intellijDep = configurations.named("intellijPlatformDependency")
        intellijPlatformJars.from(intellijDep)

        // Compute IDEA lib directory ONCE at configuration time.
        // This prevents resolving IntelliJ SDK during task execution (config-cache friendly).
        val platformFiles = intellijDep.get().files
        val platformLib = platformFiles.find { it.name.startsWith("idea") && it.isDirectory }?.resolve("lib")
            ?: platformFiles.find { it.name.startsWith("idea") && it.name.endsWith(".jar") }?.parentFile

        require(platformLib != null && platformLib.exists()) {
            "Cannot locate IntelliJ IDEA 'lib' directory from intellijPlatformDependency."
        }
        ideaLibDir.set(platformLib)

        // Previously: classpath(grammarKitConfig, files("lib/psiImplUtils.jar"), intellijPlatformJars)
        // Now: keep only small jars as task inputs; IDEA libs are runtime-only and Internal.
        grammarKitClasspath.from(grammarKitConfig)
        psiImplUtilsJar.from(files("buildLib/psiImplUtils.jar"))

        bnfFile.set(layout.projectDirectory.file(bnfPath))
        outputDirs.set(outputDirPaths)
        outputDirPaths.forEach { outputs.dir(layout.projectDirectory.dir(it)) } // keep as you had
    }

val generateLsfLexer by tasks.registerLexerTask(
    "generateLsfLexer",
    "src/com/lsfusion/lang/LSF.flex",
    "gen/com/lsfusion/lang",
    "gen/com/lsfusion/lang/LSFLexer.java"
)

val generateMigrationLexer by tasks.registerLexerTask(
    "generateMigrationLexer",
    "src/com/lsfusion/migration/Migration.flex",
    "gen/com/lsfusion/migration",
    "gen/com/lsfusion/migration/MigrationLexer.java"
)

val generateLsfParser by tasks.registerParserTask(
    "generateLsfParser",
    "src/com/lsfusion/lang/LSF.bnf",
    listOf("gen/com/lsfusion/lang/parser", "gen/com/lsfusion/lang/psi")
)

val generateMigrationParser by tasks.registerParserTask(
    "generateMigrationParser",
    "src/com/lsfusion/migration/Migration.bnf",
    listOf("gen/com/lsfusion/migration/lang/parser", "gen/com/lsfusion/migration/lang/psi")
)

tasks.withType<JavaCompile>().configureEach {
    dependsOn(generateLsfLexer, generateMigrationLexer, generateLsfParser, generateMigrationParser)
    finalizedBy(packPsiImplUtils)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateLsfLexer, generateMigrationLexer, generateLsfParser, generateMigrationParser)
    finalizedBy(packPsiImplUtils)
}
