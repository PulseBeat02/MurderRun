# Configuring Murder Run
Murder Run has thousands of configuration options. Here's a brief guide for each configuration file and what they do.
All of these files are created in the Murder Run data folder (`plugins/MurderRun`) the first time the server starts.

```{figure} images/files.png
Standard Murder Run Files
```

## Permissions
Each command has its own permission node. See the [Commands and Permissions](commands.md) page for the full list.

## Plugin Configuration
The plugin configuration file is stored as `config.yml` under the Murder Run data folder. There are comments in the
configuration file that specify what each option does.

| Option                     | Default           | What it does                                                                          |
|----------------------------|-------------------|---------------------------------------------------------------------------------------|
| `language`                 | `EN_US`           | The plugin language: `EN_US`, `ES_ES`, `ZH_CN` (Simplified Chinese), or `ZH_HK` (Traditional Chinese) |
| `pack-provider`            | `MC_PACK_HOSTING` | How the resource pack is served to players (see below)                                |
| `server.host-name`         | `localhost`       | Host name used by the `LOCALLY_HOSTED_DAEMON` pack provider                           |
| `server.port`              | `7270`            | Port used by the `LOCALLY_HOSTED_DAEMON` pack provider                                |
| `relational-data-provider` | `JSON`            | Where plugin data is stored: `JSON` files, or an `SQL` database                       |
| `database-options`         |                   | The JDBC driver, URL, database name, username, and password used when the data provider is `SQL` |

The resource pack can be served in three ways:
- `MC_PACK_HOSTING` uploads the pack to [mc-packs.net](https://mc-packs.net/) and caches the link. It doesn't need any
  port-forwarding, but it depends on that website being reachable.
- `ON_SERVER` serves the pack from your Minecraft server's own port. If your server is public, that port must be
  port-forwarded, and for LAN servers you must set `server-ip` in `server.properties` to your device's IP.
- `LOCALLY_HOSTED_DAEMON` starts a small web server on `server.host-name` and `server.port`, which needs its own
  port-forwarded port.

## Locale
The locale file is stored under the `locale` folder under the Murder Run data folder, named after the language chosen
in `config.yml` (for example, `locale/murderrun_en_us.properties`). You're able to change any messages, color,
formatting, to your heart's desire as much as you want. Any message you delete falls back to the default one. These
messages use the [MiniMessage](https://docs.advntr.dev/minimessage/format) format. You can use an easy text converter
[here](https://webui.advntr.dev/), which allows you to color text and create components.

## Game Properties
Game settings are stored in `.game.properties` files under the Murder Run data folder. These properties files contain
all in-game specific options, and there are comments in each file which specify what each configuration option does,
and how to configure it.

| File                         | Applies to                                                                     |
|------------------------------|--------------------------------------------------------------------------------|
| `common.game.properties`     | All game modes. Includes the NPC shop skins and the disabled gadgets and abilities |
| `default.game.properties`    | The default game mode                                                          |
| `one_bounce.game.properties` | The One Bounce game mode                                                       |
| `freeze_tag.game.properties` | The Freeze Tag game mode                                                       |

Some commonly changed options are:
- `disabled_gadgets` and `disabled_abilities` (in `common.game.properties`) take a comma-separated list of gadget or
  ability ids to disable. You can find the ids with the `/murder gadget retrieve` and `/murder ability retrieve`
  commands.
- `vault.reward` sets the money each winner receives if [Vault](https://github.com/MilkBowl/Vault) is installed. Set it
  to `-1` to disable rewards.
- `game.random_events.enabled` turns random in-game events on or off.
- `game.utilities.random` and the `game.utilities.*` counts control whether players are given random gadgets and
  abilities, and how many.
- The `nexo.*` and `craftengine.*` options let you replace the currency, ghost bone, and killer/survivor gear with
  custom items from [Nexo](https://nexomc.com/) or [CraftEngine](https://github.com/Xiao-MoMi/craft-engine). Leave them
  as `none` to use the default items.

## Resource Pack
The resource pack is stored as `pack.zip` under the Murder Run data folder. The `pack.zip` file is a zipped pack of all
the resources that will be sent to users when the game starts. If you want to edit the resource-pack, unzip the
`pack.zip` file and change what you need. Re-zip your changes, and make sure that the zip file is named `pack.zip`
still and in the same exact directory. Murder Run will apply your changes and send them to users.

```{note}
The pack targets Minecraft 26.3 (resource pack format 97). If you edit `pack.mcmeta`, keep the `min_format` and
`max_format` fields, since Minecraft requires them for packs made for 1.21.9 and newer.
```

## Quick Join Configuration
The quick-join configuration is stored as `quick-join.yml` under the Murder Run data folder. Quick-join is **disabled
by default**, so set `enabled: true` to use it.

| Option              | Default                              | What it does                                              |
|---------------------|--------------------------------------|-----------------------------------------------------------|
| `enabled`           | `false`                              | Whether `/murder game quick-join` is allowed               |
| `min-players`       | `2`                                  | Minimum players for a new quick-join game                  |
| `max-players`       | `16`                                 | Maximum players for a new quick-join game                  |
| `game-modes`        | `ONE_BOUNCE`, `DEFAULT`, `FREEZE_TAG` | Game modes a new quick-join game is randomly chosen from  |
| `arena-lobby-pairs` | none                                 | Arena and lobby pairs, written as `["ArenaName", "LobbyName"]` |

When players run the `/murder game quick-join` command and there isn't an open quick-joinable game, a new game is
created with a random arena-lobby pair from this file. Please note that the arena-lobby pairs both MUST BE VALID ARENAS
AND LOBBIES. If Murder Run doesn't recognize the arena or lobby, it silently skips the pair as if it never existed. You
must have existing arenas and lobbies created before you can configure this file, because Murder Run parses this file
on start-up.

---

# Other Additional Files
Other additional files include other files that you shouldn't configure, and are just meant for storing data.

## Schematic Storage
The `schematics` folder under the Murder Run data folder contains two folders named `lobbies` and `arenas`. The `lobbies`
folder contains lobby schematics, while the `arenas` folder contains arena schematics.

## Plugin Data
When `relational-data-provider` is set to `JSON` (the default), plugin data is stored in these files under the Murder
Run data folder. When it's set to `SQL`, the same data is stored in your database instead.

| File                     | Contents                                                                  |
|--------------------------|---------------------------------------------------------------------------|
| `arenas.json`            | All arena information, such as their name, origin, bounds, part locations, etc. |
| `lobbies.json`           | All lobby information, such as their name, origin, and bounds              |
| `arenas-creation.json`   | Each player's last arena creation settings used in the GUI                 |
| `player-statistics.json` | All player statistics                                                      |

## Resource Pack Caches
`pack-hashes.json` stores the hash of your `pack.zip` so it's only recalculated when the pack changes, and
`cached-packs.json` stores the link to your uploaded pack when using `MC_PACK_HOSTING`.

## Demo Zip
The `demo-setup.zip` file under the Murder Run data folder contains the demo worlds and data used when the player creates
a test lobby and arena demo using the `/murder demo` command.
