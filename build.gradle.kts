import xyz.jpenilla.runtask.task.AbstractRun

plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.checkerframework") version "0.6.43"
    id("com.diffplug.spotless") version "7.0.0.BETA1"
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
}

dependencies {
    annotationProcessor("org.incendo:cloud-annotations:2.0.0-SNAPSHOT")

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("fastutil:fastutil:5.0.9")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.6")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.6")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    implementation(project(":nms-api"))
    implementation(project(":v1_21_1"))
    implementation("org.bstats:bstats-bukkit:3.0.3")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
    implementation("team.unnamed:creative-api:1.7.3")
    implementation("team.unnamed:creative-serializer-minecraft:1.7.3")
    implementation("team.unnamed:creative-server:1.7.3")
    implementation("org.incendo:cloud-annotations:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-SNAPSHOT")
    implementation("me.lucko:commodore:2.2")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("fr.skytasul:glowingentities:1.3.5")

    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.110.0")
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
        dependsOn("spotlessApply")
        dependsOn(":v1_21_1:reobfJar")
        dependsOn("shadowJar")
    }

    runServer {
        downloadPlugins {
            url("https://cdn.modrinth.com/data/1u6JkXh5/versions/yAujLUIK/worldedit-bukkit-7.3.6.jar")
            url("https://ci.citizensnpcs.co/job/Citizens2/lastSuccessfulBuild/artifact/dist/target/Citizens-2.0.35-b3519.jar")
        }
        minecraftVersion("1.21.1")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        rename("plugin.json", "plugin.yml")
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.json") {
            expand(props)
        }
    }

    shadowJar {

        // Disable Relocations Temporary for Hot Swapping
        //        relocate("net.kyori", "io.github.pulsebeat02.murderrun.lib.net.kyori")
        //        relocate("team.unnamed", "io.github.pulsebeat02.murderrun.lib.team.unnamed")
        //        relocate("org.incendo", "io.github.pulsebeat02.murderrun.lib.org.incendo")
        //        relocate("me.lucko", "io.github.pulsebeat02.murderrun.lib.me.lucko")
        //        relocate("io.leangen", "io.github.pulsebeat02.murderrun.lib.io.leangen")
        //        relocate("org.jsoup", "io.github.pulsebeat02.murderrun.lib.org.jsoup")
        //        relocate("fr.skytasul", "io.github.pulsebeat02.murderrun.lib.fr.skytasul")
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