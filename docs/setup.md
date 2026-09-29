# Setting up a Server

```{warning}
Murder Run only supports **Minecraft 26.3** as of right now. Make sure to choose **Paper 26.3**. Older servers will
refuse to load the plugin with an `Unsupported API version 26.3` error. Support for future versions will be added as
fast as possible.
```

```{note}
Paper's 26.3 builds are still marked as experimental. Back up your worlds before updating, since a world that has been
opened on 26.3 can't be opened on an older version again.
```

To set up Murder Run, you will need to do two things:
1) Download a Minecraft Paper-Based Server
2) Download the Murder Run Plugin JAR

## Requirements
- **Minecraft / Paper:** 26.3 (Paper or a Paper fork)
- **Java:** 25 or newer
- **Required plugins:** WorldEdit, Citizens, and PacketEvents (downloaded automatically, see [Dependencies](#dependencies))

## Setting up a Paper-Based Server
In order to get a Minecraft Paper-based Server, you need to download either Paper, or any other Paper-fork
software. I recommend downloading [Paper](https://papermc.io/) -- it's also the software I develop against actively
for Murder Run.

```{figure} images/papermc.png
PaperMC Team
```

To set up a Paper server, refer to the [Getting Started Guide](https://docs.papermc.io/paper/getting-started) that Paper
has posted on their website. It includes a very detailed step-by-step guide to set up the server onto your computer,
and how to add plugins. Please make sure that you use Java 25, as it is required by this plugin!

## Getting Murder Run
Murder Run is like any other plugin, where you just drag and drop the JAR file into the plugins folder. There isn't any
set up required, and Murder Run will download the necessary dependencies for you. You can find bleeding-edge releases
on the TeamCity CI [here](https://ci.brandonli.me/repository/download/murderrun/.lastFinished/MurderRun-26.3-v1.0.0-all.jar),
which are created pretty frequently. You should always use the latest Murder Run JAR, as it contains many more bug fixes
and version compatibility than the previous.

## Dependencies
Murder Run has three essential dependencies, [WorldEdit](https://enginehub.org/worldedit/), [Citizens](https://citizensnpcs.co/),
and [PacketEvents](https://github.com/retrooper/packetevents/). All are necessary in order for the plugin to function.

If any of them are missing when the server starts, Murder Run automatically downloads them into your plugins folder
and loads them, so Murder Run itself works without a restart:

| Plugin       | Downloaded from                                          | Versions that support 26.3              |
|--------------|----------------------------------------------------------|-----------------------------------------|
| WorldEdit    | [Modrinth](https://modrinth.com/plugin/worldedit)        | 7.4.6 (currently a beta) or newer       |
| PacketEvents | [Modrinth](https://modrinth.com/plugin/packetevents)     | 2.14.0 or newer                         |
| Citizens     | [Jenkins CI](https://ci.citizensnpcs.co/job/Citizens2/)  | 2.0.44 development builds or newer      |

WorldEdit and PacketEvents use the newest Modrinth build that lists your server's Minecraft version, preferring full
releases over betas, so a beta is only used when no release supports your version yet. Citizens is fetched from the
latest successful build on its Jenkins CI, which doesn't list the Minecraft versions it supports, so if that build
doesn't work on your server, Murder Run removes the jar it downloaded when the server stops and asks you to install a
compatible version manually.

Plugins you have already installed are never replaced or removed, and [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit)
counts as WorldEdit. If you install one of these plugins yourself, make sure it's a version that supports 26.3 (see the
table above). If you have other plugins that depend on WorldEdit, Citizens, or PacketEvents, restart the server once
after the first start so those plugins load correctly.

If a required plugin can't be downloaded (for example, if your server has no internet access) or fails to load,
Murder Run will print which plugins are missing and disable itself. In that case, download them manually, drop them
into the plugins folder, and restart the server.

```{figure} images/citizens.png
Citizens Plugin
```

## Optional Integrations
Murder Run also hooks into several other plugins if they are installed. None of them are required.

| Plugin                                                               | What Murder Run uses it for                                                    |
|----------------------------------------------------------------------|--------------------------------------------------------------------------------|
| [LibsDisguises](https://github.com/libraryaddict/LibsDisguises)      | Required by the `mimic` gadget, which is removed when LibsDisguises isn't installed. Use 26.9.24 or newer on 26.3. |
| [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi)         | Player statistic placeholders (see [PlaceholderAPI Support](placeholderapi.md)) |
| [Parties](https://alessiodp.com/parties)                             | Creating games from your party with `/murder game party`                       |
| [Vault](https://github.com/MilkBowl/Vault)                           | Paying winners the `vault.reward` set in the game properties                   |
| [Nexo](https://nexomc.com/) / [CraftEngine](https://github.com/Xiao-MoMi/craft-engine) | Sends their resource pack together with Murder Run's, and lets you swap the currency, ghost bone, and killer/survivor gear for your own custom items (the `nexo.*` / `craftengine.*` game properties) |
| [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit) | Used in place of WorldEdit if installed                                        |

Once you're done reading, take a look at the [configuration](configuration.md) page!
