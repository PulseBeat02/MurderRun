# Getting Started

As of right now, Murder Run has a small developer API used for interacting with the game. In order to import Murder Run, 
first use JitPack to import the repository.

First, add the JitPack repository:

**build.gradle**
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**build.gradle.kts**
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**pom.xml**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then, add the plugin dependency to your project:

**build.gradle**
```groovy
dependencies {
    implementation 'com.github.PulseBeat02:MurderRun:Tag'
}
```

**build.gradle.kts**
```kotlin
dependencies {
    implementation("com.github.PulseBeat02:MurderRun:Tag")
}
```

**pom.xml**
```xml
<dependency>
    <groupId>com.github.PulseBeat02</groupId>
    <artifactId>MurderRun</artifactId>
    <version>Tag</version>
</dependency>
```

Take a look [here](api.md) for the API documentation.