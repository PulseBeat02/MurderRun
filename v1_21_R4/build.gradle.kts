plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.13"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly(project(":nms-api"))
}