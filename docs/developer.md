# Getting Started

As of right now, Murder Run has a small developer API used for interacting with the game. Builds are published to
the `repo.brandonli.me` Maven repository.

First, add the repository:

**build.gradle.kts**
```kotlin
repositories {
    maven("https://repo.brandonli.me/snapshots")
}
```

Then, add the plugin dependency to your project. Use `compileOnly`, since Murder Run is installed on the server as its
own plugin and shouldn't be shaded into yours:

**build.gradle.kts**
```kotlin
dependencies {
    compileOnly("me.brandonli:MurderRun:26.3-v1.0.0")
}
```

Murder Run is built against Minecraft 26.3 and Java 25, so your plugin needs to target Java 25 or newer as well.

Finally, declare Murder Run as a dependency of your plugin, so that it loads before your plugin does:

**paper-plugin.yml**
```yaml
dependencies:
  server:
    MurderRun:
      load: BEFORE
      required: true
      join-classpath: true
```

**plugin.yml** (if you're not using a Paper plugin)
```yaml
depend: [MurderRun]
```

Take a look [here](api.md) for the API documentation.
