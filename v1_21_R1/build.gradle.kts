plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    setOf(test, compileTestJava, testClasses).forEach {
        it {
            enabled = false
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly(project(":nms-api"))
}