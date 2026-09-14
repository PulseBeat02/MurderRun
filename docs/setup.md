# Setting up a Server

```{warning}
Murder Run only supports **Minecraft 26.2** as of right now. If you choose any other version, the plugin will **not**
start-up, and it will break. Make sure to choose **Paper 26.2** for now. Support for future versions will be added as
fast as possible.
```

To set up Murder Run, you will need to do two things:
1) Download a Minecraft Paper-Based Server
2) Download the Murder Run Plugin JAR

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
on the TeamCity CI [here](https://ci.brandonli.me/repository/download/murderrun/.lastFinished/MurderRun-26.2-v1.0.0-all.jar),
which are created pretty frequently. You should always use the latest Murder Run JAR, as it contains many more bug fixes 
and version compatability than the previous.

## Dependencies
Murder Run has three essential dependencies, [WorldEdit](https://enginehub.org/worldedit/), [Citizens](https://citizensnpcs.co/),
and [PacketEvents](https://github.com/retrooper/packetevents/). All are necessary in order for the plugin to function.

If any of them are missing when the server starts, Murder Run automatically downloads them into your plugins folder
and loads them, so Murder Run itself works without a restart. WorldEdit and PacketEvents are fetched from
[Modrinth](https://modrinth.com/), using the latest build that supports your server's Minecraft version. Citizens is
fetched from the latest build on its [Jenkins CI](https://ci.citizensnpcs.co/job/Citizens2/), which doesn't list the
Minecraft versions it supports, so if that build doesn't work on your server, Murder Run removes the jar it downloaded
when the server stops and asks you to install a compatible version manually. 

Plugins you have already installed are never replaced or
removed, and [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit) counts as WorldEdit. If you have
other plugins that depend on WorldEdit, Citizens, or PacketEvents, restart the server once after the first start so
those plugins load correctly.

If a required plugin can't be downloaded (for example, if your server has no internet access) or fails to load,
Murder Run will print which plugins are missing and disable itself. In that case, download them manually, drop them
into the plugins folder, and restart the server.

```{figure} images/citizens.png
Citizens Plugin
```

Murder Run also hooks into several other plugins, like Libsdisguises, PlaceholderAPI, Parties, Nexo, Citizens, and many
other plugins for features. You are able to see some of these features later on.

Once you're done reading, take a look at the [configuration](configuration.md) page!
