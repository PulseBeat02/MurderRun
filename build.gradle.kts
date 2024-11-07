import xyz.jpenilla.runtask.task.AbstractRun

plugins {
    java
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.checkerframework") version "0.6.46"
    id("com.diffplug.spotless") version "7.0.0.BETA4"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("com.github.node-gradle.node") version "7.1.0"
}

apply(plugin = "org.checkerframework")

group = "io.github.pulsebeat02"
version = "1.21.3-v1.0.0"
description = "MurderRun"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

val runtimeDeps = listOf(
    libs.adventureApi,
    libs.adventurePlatformBukkit,
    libs.adventureTextMinimessage,
    libs.adventureTextSerializerPlain,
    libs.adventureTextSerializerLegacy,
    libs.creativeApi,
    libs.creativeSerializerMinecraft,
    libs.creativeServer,
    libs.cloudAnnotations,
    libs.cloudPaper,
    libs.cloudMinecraftExtras,
    libs.commodore,
    libs.jsoup,
    libs.fastboard,
    libs.inventoryFramework,
    libs.bstatsBukkit,
    libs.hibernateCore,
    libs.mysqlConnector,
    libs.h2,
    libs.postgresql,
    libs.sqliteJdbc,
    libs.byteBuddy,
    libs.byteBuddyAgent
)

dependencies {

    // Annotation Processors
    annotationProcessor(libs.cloudAnnotations)

    // Project Dependencies
    implementation(project(":nms-api"))
    runtimeOnly(project(":v1_21_R3", "reobf"))

    // Provided Dependencies
    compileOnly(libs.spigotApi)
    compileOnly(libs.fastutil)
    compileOnly(libs.nettyAll)
    runtimeDeps.forEach(::compileOnly)

    // Plugin Extensions
    compileOnly(libs.placeholderapi)
    compileOnly(libs.protocolLib)
    compileOnly(libs.libsDisguises)
    compileOnly(libs.worldeditCore)
    compileOnly(libs.worldeditBukkit)
    compileOnly(libs.citizensMain) {
        exclude(
            group = "*",
            module = "*"
        )
    }

    // Testing Dependencies
    testImplementation(libs.creativeApi)
    testImplementation(libs.creativeSerializerMinecraft)
    testImplementation(libs.creativeServer)
    testImplementation(libs.jsoup)
    testImplementation(libs.fastutil)
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    val language = JavaLanguageVersion.of(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    toolchain.languageVersion.set(language)
}

tasks.withType<AbstractRun>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    })
    jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+AllowRedefinitionToAddDeleteMethods")
}

val windows = System.getProperty("os.name").lowercase().contains("windows")
tasks {

    bukkitPluginYaml {
        val updatedLibraries = runtimeDeps.stream().map { it.get().toString() }.toList()
        name = "MurderRun"
        version = "${project.version}"
        description = "Pulse's MurderRun Plugin"
        authors = listOf("PulseBeat_02")
        apiVersion = "1.21"
        prefix = "Murder Run"
        main = "io.github.pulsebeat02.murderrun.MurderRun"
        softDepend = listOf(
            "WorldEdit",
            "WorldEditTickSpreader",
            "Citizens",
            "LibsDisguises",
            "PlaceholderAPI")
        libraries = updatedLibraries
    }

    withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
        options.isFork = true
        options.forkOptions.memoryMaximumSize = "4g"
    }

    assemble {
        dependsOn(":v1_21_R3:reobfJar")
        dependsOn("shadowJar")
    }

    build {
        dependsOn("spotlessApply")
    }

    runServer {
        downloadPlugins {
            url("https://ci.md-5.net/job/LibsDisguises/lastSuccessfulBuild/artifact/target/LibsDisguises.jar")
            url("https://ci.extendedclip.com/job/PlaceholderAPI/lastSuccessfulBuild/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-200.jar")
        }
        systemProperty("net.kyori.adventure.text.warnWhenLegacyFormattingDetected", false)
        minecraftVersion("1.21.3")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = "UTF-8"
    }

    spotlessInternalRegisterDependencies {
        dependsOn("nodeSetup", "npmSetup")
    }

    sourceSets {
        main {
            java.srcDir("src/main/java")
            resources.srcDir("src/main/resources")
        }
    }

    spotless {
        java {
            importOrder()
            removeUnusedImports()
            prettier(mapOf("prettier" to "3.3.3", "prettier-plugin-java" to "2.6.4"))
                .config(mapOf("parser" to "java",
                    "tabWidth" to 2,
                    "plugins" to listOf("prettier-plugin-java"),
                    "printWidth" to 140))
                .nodeExecutable(provider { setupNodeEnvironment() })
            licenseHeaderFile("HEADER")
        }
    }

    shadowJar {
        dependencies {
            include(project(":v1_21_R3"))
            include(project(":nms-api"))
        }
    }

    checkerFramework {
        checkers = listOf("org.checkerframework.checker.nullness.NullnessChecker")
        extraJavacArgs = listOf(
            "-AsuppressWarnings=uninitialized",
            "-Astubs=${project.file("checker-framework")}"
        )
    }

    node {
        download = true
        version = "22.9.0"
        workDir = file("build/nodejs")
    }
}

fun setupNodeEnvironment(): File {
    val npmExec = if (windows) "node.exe" else "bin/node"
    val folder = node.resolvedNodeDir.get()
    val executable = folder.file(npmExec).asFile
    return executable
}