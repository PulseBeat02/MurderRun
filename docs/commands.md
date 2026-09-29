# Commands and Permissions
Every Murder Run command starts with `/murder`, and every command has its own permission node. Arguments in `<angle
brackets>` are required, and arguments in `[square brackets]` are optional. Run `/murder help` in-game to see the
commands you have access to, or `/murder help <query>` to search them.

```{note}
Murder Run doesn't give any permissions to players by default, so only operators can use its commands until you grant
permissions with a permissions manager like [LuckPerms](https://luckperms.net/). For example, to let everyone join and
play games, give the default group the "Player" permissions listed below. Most permission managers also accept the
`murderrun.command.*` wildcard to grant everything at once.
```

## Player Commands
These are the commands regular players need to join and play games.

| Command                         | Permission                              | Description                                                   |
|---------------------------------|-----------------------------------------|---------------------------------------------------------------|
| `/murder help [query]`          | `murderrun.command.help`                | Shows the commands you can use                                |
| `/murder game join <id>`        | `murderrun.command.game.player.join`    | Joins the game with that id after you've been invited         |
| `/murder game quick-join`       | `murderrun.command.game.quick-join`     | Joins the next open quick-join game (see [Quick-join](game.md#quick-join-commands)) |
| `/murder game leave`            | `murderrun.command.game.leave`          | Leaves your current game                                      |
| `/murder game list`             | `murderrun.command.game.list`           | Lists all survivors and killers in your current game          |
| `/murder resources`             | `murderrun.command.resources`           | Sends you the resource pack if you don't have it loaded       |

## Game Owner Commands
These commands are used by the player who creates and manages a game.

| Command                                                                     | Permission                          | Description                                           |
|-----------------------------------------------------------------------------|-------------------------------------|-------------------------------------------------------|
| `/murder game create <arena> <lobby> <id> <mode> <min> <max> <quick-joinable>` | `murderrun.command.game.create`  | Creates a new game (see [Creating Games](game.md))    |
| `/murder game party <arena> <lobby>`                                        | `murderrun.command.game.party`      | Creates a game with everyone in your Parties party    |
| `/murder game invite <player>`                                              | `murderrun.command.game.invite`     | Invites a player to your game                         |
| `/murder game kick <player>`                                                | `murderrun.command.game.kick`       | Kicks a player from your game                         |
| `/murder game set murderer <player>`                                        | `murderrun.command.game.set.killer` | Makes a player a killer                               |
| `/murder game set innocent <player>`                                        | `murderrun.command.game.set.survivor` | Makes a player a survivor                           |
| `/murder game gui`                                                          | `murderrun.command.game.gui`        | Opens a GUI to invite players and set their roles     |
| `/murder game start`                                                        | `murderrun.command.game.start`      | Starts your game once enough players have joined      |
| `/murder game cancel`                                                       | `murderrun.command.game.cancel`     | Cancels your game, resetting the map and players      |

## Setup Commands
These commands are used by admins to create lobbies, arenas, and NPC shops. See
[Creating Lobbies and Arenas](creation.md) for a step-by-step guide.

| Command                                  | Permission                                  | Description                                            |
|------------------------------------------|---------------------------------------------|--------------------------------------------------------|
| `/murder gui`                            | `murderrun.command.gui`                     | Opens the GUI for creating lobbies, arenas, and games  |
| `/murder demo`                           | `murderrun.command.demo`                    | Loads the demo lobby and arena (run it twice to confirm) |
| `/murder lobby set name <name>`          | `murderrun.command.lobby.set.name`          | Sets the name of the lobby you're creating             |
| `/murder lobby set spawn`                | `murderrun.command.lobby.set.spawn`         | Sets the lobby spawn to your location                  |
| `/murder lobby set first-corner`         | `murderrun.command.lobby.set.corner.first`  | Sets the first corner of the lobby to your location    |
| `/murder lobby set second-corner`        | `murderrun.command.lobby.set.corner.second` | Sets the second corner of the lobby to your location   |
| `/murder lobby create`                   | `murderrun.command.lobby.create`            | Creates (or replaces) the lobby                        |
| `/murder lobby list`                     | `murderrun.command.lobby.list`              | Lists all lobbies                                      |
| `/murder lobby remove <name>`            | `murderrun.command.lobby.remove`            | Removes a lobby                                        |
| `/murder arena set name <name>`          | `murderrun.command.arena.set.name`          | Sets the name of the arena you're creating             |
| `/murder arena set spawn`                | `murderrun.command.arena.set.spawn`         | Sets the arena spawn to your location                  |
| `/murder arena set truck`                | `murderrun.command.arena.set.truck`         | Sets the truck location to your location               |
| `/murder arena set first-corner`         | `murderrun.command.arena.set.corner.first`  | Sets the first corner of the arena to your location    |
| `/murder arena set second-corner`        | `murderrun.command.arena.set.corner.second` | Sets the second corner of the arena to your location   |
| `/murder arena set item add`             | `murderrun.command.arena.set.item.add`      | Adds your location as an item spawn                    |
| `/murder arena set item remove`          | `murderrun.command.arena.set.item.remove`   | Removes the item spawn at your location                |
| `/murder arena set item list`            | `murderrun.command.arena.set.item.list`     | Lists all item spawns                                  |
| `/murder arena create`                   | `murderrun.command.arena.create`            | Creates (or replaces) the arena                        |
| `/murder arena copy <name>`              | `murderrun.command.arena.copy`              | Copies an existing arena's settings so you can edit them |
| `/murder arena list`                     | `murderrun.command.arena.list`              | Lists all arenas                                       |
| `/murder arena remove <name>`            | `murderrun.command.arena.remove`            | Removes an arena                                       |
| `/murder npc spawn gadget killer`        | `murderrun.command.npc.spawn.gadget.killer`   | Spawns the NPC that sells killer gadgets             |
| `/murder npc spawn gadget survivor`      | `murderrun.command.npc.spawn.gadget.survivor` | Spawns the NPC that sells survivor gadgets           |
| `/murder npc spawn ability killer`       | `murderrun.command.npc.spawn.ability.killer`  | Spawns the NPC that sells killer abilities           |
| `/murder npc spawn ability survivor`     | `murderrun.command.npc.spawn.ability.survivor` | Spawns the NPC that sells survivor abilities        |
| `/murder npc remove`                     | `murderrun.command.npc.remove`              | Removes the Murder Run NPC closest to you              |

## Admin and Debugging Commands
These commands are meant for testing and troubleshooting.

| Command                                | Permission                                | Description                                              |
|----------------------------------------|-------------------------------------------|----------------------------------------------------------|
| `/murder gadget menu`                  | `murderrun.command.gadget.menu`           | Opens a GUI with every gadget; click one to get it       |
| `/murder gadget retrieve <gadget>`     | `murderrun.command.gadget.retrieve`       | Gives you a gadget by its id                             |
| `/murder gadget retrieve-all`          | `murderrun.command.gadget.retrieve-all`   | Gives you every gadget (this overflows your inventory)   |
| `/murder ability menu`                 | `murderrun.command.ability.menu`          | Opens a GUI with every ability; click one to get it      |
| `/murder ability retrieve <ability>`   | `murderrun.command.ability.retrieve`      | Gives you an ability by its id                           |
| `/murder ability retrieve-all`         | `murderrun.command.ability.retrieve-all`  | Gives you every ability                                  |
| `/murder truck render`                 | `murderrun.command.truck.render`          | Renders an example truck out of block displays near you  |
| `/murder truck destroy`                | `murderrun.command.truck.destroy`         | Removes rendered example trucks near you                 |
| `/murder dump`                         | `murderrun.command.dump`                  | Uploads server information for support (see below)       |

```{warning}
`/murder dump` uploads your server information, Java system properties, and `logs/latest.log` to
[paste.helpch.at](https://paste.helpch.at/) and gives you a link to share in the support Discord. Anyone with the link
can read it, so check your log for anything private before sharing it.
```
