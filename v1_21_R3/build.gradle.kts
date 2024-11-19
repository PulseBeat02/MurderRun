plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    compileOnly(project(":nms-api"))
}