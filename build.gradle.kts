import xyz.jpenilla.runtask.task.AbstractRun

plugins {
    java
    `java-library`
    id("com.gradleup.shadow") version "8.3.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.checkerframework") version "0.6.45"
    id("com.diffplug.spotless") version "7.0.0.BETA2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

apply(plugin = "org.checkerframework")

group = "io.github.pulsebeat02"
version = "1.21.1-v1.0.0"
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

var runtimeDeps = listOf(
    "net.kyori:adventure-api:4.17.0",
    "net.kyori:adventure-platform-bukkit:4.3.4",
    "net.kyori:adventure-text-minimessage:4.17.0",
    "net.kyori:adventure-text-serializer-plain:4.17.0",
    "net.kyori:adventure-text-serializer-legacy:4.17.0",
    "net.kyori:adventure-text-serializer-gson:4.17.0",
    "team.unnamed:creative-api:1.7.3",
    "team.unnamed:creative-serializer-minecraft:1.7.3",
    "team.unnamed:creative-server:1.7.3",
    "org.incendo:cloud-annotations:2.0.0",
    "org.incendo:cloud-paper:2.0.0-beta.10",
    "org.incendo:cloud-minecraft-extras:2.0.0-beta.10",
    "me.lucko:commodore:2.2",
    "org.jsoup:jsoup:1.18.1",
    "com.github.stefvanschie.inventoryframework:IF:0.10.17",
    "org.hibernate.orm:hibernate-core:7.0.0.Beta1",
    "com.mysql:mysql-connector-j:9.0.0",
    "net.megavex:scoreboard-library-api:2.1.12",
    "net.megavex:scoreboard-library-implementation:2.1.12",
    "net.megavex:scoreboard-library-packetevents:2.1.12",
    "org.bstats:bstats-bukkit:3.1.0"
);

bukkit {

    name = "MurderRun"
    version = "1.21.1-v1.0.0"
    description = "Pulse's MurderRun Plugin"
    authors = listOf("PulseBeat_02")
    apiVersion = "1.21"
    prefix = "Murder Run"
    main = "io.github.pulsebeat02.murderrun.MurderRun"
    softDepend = listOf(
        "WorldEdit",
        "Citizens",
        "LibsDisguises",
        "PlaceholderAPI",   "ProtocolLib",
                "ProtocolSupport",
                "ViaVersion",
                "ViaBackwards",
                "ViaRewind",
                "Geyser-Spigot", "PacketEvents")
}

dependencies {

    annotationProcessor("org.incendo:cloud-annotations:2.0.0")

    implementation(project(":nms-api"))
    implementation(project(":v1_21_R1"))
    runtimeDeps.forEach(::implementation)

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("fastutil:fastutil:5.0.9")
    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("io.netty:netty-codec-http:4.1.97.Final")

    // Plugin Extensions
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("LibsDisguises:LibsDisguises:10.0.44")
    compileOnly("com.github.retrooper:packetevents-spigot:2.5.0")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.6")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.6")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude(
            group = "*",
            module = "*"
        )
    }

    testImplementation("team.unnamed:creative-api:1.7.3")
    testImplementation("team.unnamed:creative-serializer-minecraft:1.7.3")
    testImplementation("team.unnamed:creative-server:1.7.3")
    testImplementation("org.jsoup:jsoup:1.18.1")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<AbstractRun>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    })
    jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+AllowRedefinitionToAddDeleteMethods")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
    options.isFork = true
    options.forkOptions.memoryMaximumSize = "4g"
}

tasks {

    assemble {
        dependsOn(":v1_21_R1:reobfJar")
        dependsOn("shadowJar")
    }

    build {
        dependsOn("spotlessApply")
    }

    runServer {
        downloadPlugins {
            url("https://ci.md-5.net/job/LibsDisguises/lastSuccessfulBuild/artifact/target/LibsDisguises.jar")
            url("https://ci.extendedclip.com/job/PlaceholderAPI/lastSuccessfulBuild/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-200.jar")
            // aka im lazy as fck lmao
        }
        minecraftVersion("1.21.1")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "io.github.pulsebeat02.murderrun.lib.org.bstats")
        dependencies {
            exclude(dependency("com.google.code.gson:gson:.*"))
            exclude(dependency("com.mojang:brigadier:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }
    }
}

spotless {
    java {
        palantirJavaFormat("2.47.0").style("GOOGLE")
    }
}

sourceSets {
    main {
        java.srcDir("src/main/java")
        resources.srcDir("src/main/resources")
    }
}

checkerFramework {
    checkers = listOf("org.checkerframework.checker.nullness.NullnessChecker")
    extraJavacArgs = listOf(
        "-AsuppressWarnings=uninitialized",
        "-Astubs=${project.file("checker-framework")}"
    )
}