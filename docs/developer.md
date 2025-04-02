# Getting Started

As of right now, Murder Run has a small developer API used for interacting with the game. In order to import Murder Run, 
first use JitPack to import the repository.

First, add the JitPack repository:

**build.gradle.kts**
```kotlin
repositories {
    maven("https://repo.brandonli.me/snapshots")
}
```

Then, add the plugin dependency to your project:

**build.gradle.kts**
```kotlin
dependencies { 
  implementation("me.brandonli:MurderRun:1.21.5-v1.0.0")
}
```

Take a look [here](api.md) for the API documentation.