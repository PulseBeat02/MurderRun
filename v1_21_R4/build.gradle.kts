plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.5-no-moonrise-SNAPSHOT")
    compileOnly(project(":nms-api"))
}