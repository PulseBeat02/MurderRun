import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import xyz.jpenilla.runtask.task.AbstractRun
import java.io.ByteArrayOutputStream

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.8"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.checkerframework") version "0.6.56"
    id("com.diffplug.spotless") version "7.0.0.BETA4"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0"
    id("com.github.node-gradle.node") version "7.1.0"
}

apply(plugin = "org.checkerframework")

group = "me.brandonli"
version = "1.21.8-v1.0.0"
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
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.nexomc.com/releases")
    maven("https://repo.nexomc.com/snapshots")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://jitpack.io")
}

val runtimeDeps = listOf(
    libs.adventureApi,
    libs.adventurePlatformBukkit,
    libs.adventureTextMinimessage,
    libs.adventureTextSerializerPlain,
    libs.adventureTextSerializerLegacy,
    libs.cloudAnnotations,
    libs.cloudPaper,
    libs.cloudMinecraftExtras,
    libs.commodore,
    libs.fastboard,
    libs.bstatsBukkit,
    libs.hibernateCore,
    libs.h2,
    libs.byteBuddy,
    libs.byteBuddyAgent,
    libs.classGraph,
    libs.triumphGui
)

dependencies {

    // Annotation Processors
    annotationProcessor(libs.cloudAnnotations)

    // Provided Dependencies
    compileOnly(libs.spigotApi)
    compileOnly(libs.fastutil)
    compileOnly(libs.nettyAll)
    runtimeDeps.forEach(::implementation)

    // Plugin Extensions
    compileOnly(libs.placeholderapi)
    compileOnly(libs.protocolLib)
    compileOnly(libs.libsDisguises)
    compileOnly(libs.worldeditCore)
    compileOnly(libs.worldeditBukkit)
    compileOnly(libs.partiesApi)
    compileOnly(libs.citizensMain) {
        exclude(
            group = "*",
            module = "*"
        )
    }
    compileOnly(libs.nexo)
    compileOnly(libs.adventureApi)
    compileOnly(libs.creativeSerializerMinecraft)
    compileOnly(libs.packetEvents)
    compileOnly(libs.vaultApi)

    // Testing Dependencies
    testImplementation(libs.nettyAll)
    testImplementation(libs.fastutil)
    testImplementation(libs.mockBukkit)
    testImplementation(libs.creativeSerializerMinecraft)
    testImplementation(libs.creativeApi)
    runtimeDeps.forEach(::testImplementation)
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
    jvmArgs("-Xmx2040m", "-XX:+AllowEnhancedClassRedefinition", "-XX:+AllowRedefinitionToAddDeleteMethods")
}

configurations.all {
    resolutionStrategy {
        force(libs.guava)
        force(libs.gson)
    }
}

val windows = System.getProperty("os.name").lowercase().contains("windows")

val zipPack by tasks.registering(Zip::class) {
    from("./resourcepack")
    archiveFileName.set("pack.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/pack"))
}

val zipDemo by tasks.registering(Zip::class) {
    from("./demo-setup")
    archiveFileName.set("demo-setup.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/demo-setup"))
}

tasks {

    bukkitPluginYaml {
        name = "MurderRun"
        version = "${project.version}"
        description = "Pulse's MurderRun Plugin"
        authors = listOf("PulseBeat_02")
        apiVersion = "1.21"
        prefix = "Murder Run"
        main = "me.brandonli.murderrun.MurderRun"
        softDepend = listOf(
            "WorldEdit",
            "Citizens",
            "LibsDisguises",
            "PlaceholderAPI",
            "Parties",
            "Nexo")
    }

    withType<JavaCompile>().configureEach {
        val set = setOf("-parameters", "-Xlint:deprecation", "-Xlint:unchecked")
        options.compilerArgs.addAll(set)
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
        options.isFork = true
        options.forkOptions.memoryMaximumSize = "4g"
    }

    assemble {
        dependsOn("shadowJar")
    }

    build {
        dependsOn("spotlessApply")
    }

    runServer {
        downloadPlugins {
            url("https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
            url("https://ci.md-5.net/job/LibsDisguises/lastSuccessfulBuild/artifact/target/LibsDisguises.jar")
            url("https://ci.extendedclip.com/job/PlaceholderAPI/212/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-212.jar")
            url("https://cdn.modrinth.com/data/rHRYOOoq/versions/yBAIVDGP/Parties-3.2.9.jar")
        }
        systemProperty("murderrun.development.tools", true)
        systemProperty("net.kyori.adventure.text.warnWhenLegacyFormattingDetected", false)
        minecraftVersion("1.21.8")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = "UTF-8"
        dependsOn(zipPack)
        dependsOn(zipDemo)
        val zipPack = zipPack.get().archiveFile
        val zipDemo = zipDemo.get().archiveFile
        from(zipPack) {
            into("")
        }
        from(zipDemo) {
            into("")
        }
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

    checkerFramework {
        checkers = listOf("org.checkerframework.checker.nullness.NullnessChecker")
        extraJavacArgs = listOf(
            "-AsuppressWarnings=uninitialized",
            "-Astubs=${project.file("checker-framework")}"
        )
    }

    node {
        download = true
        version = "22.12.0"
        workDir = file("build/nodejs")
    }

    fun getCurrentGitCommit(): String {
        val stdout = ByteArrayOutputStream()
        project.exec {
            executable = "git"
            args = listOf("rev-parse", "--short", "HEAD")
            standardOutput = stdout
        }
        return stdout.toString().trim()
    }

    jar {
        manifest {
            attributes("Main-Class" to "me.brandonli.murderrun.secret.Main")
            attributes["Git-Commit"] = getCurrentGitCommit()
        }
    }
}

publishing {
    repositories {
        maven {
            name = "brandonli"
            url = uri("https://repo.brandonli.me/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.brandonli"
            artifactId = project.name
            version = "${rootProject.version}"
            from(components["java"])
        }
    }
}

fun setupNodeEnvironment(): File {
    val npmExec = if (windows) "node.exe" else "bin/node"
    val folder = node.resolvedNodeDir.get()
    val executable = folder.file(npmExec).asFile
    return executable
}