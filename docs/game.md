# Creating Games
There are currently three ways to create a game. You can create a game using
1) Normal, built-in Murder Run commands
2) [Parties](https://alessiodp.com/parties) integration
3) Quick-join commands

You'll need at least one [lobby and arena](creation.md) before you can create a game. A full list of commands and
their permissions is on the [Commands and Permissions](commands.md) page.

## Built-in Commands
To create a new game, run the `/murder game create <arena> <lobby> <id> <mode> <min> <max> <quick-joinable>` command.
- The `<arena>` and `<lobby>` arguments are your arena and lobby names respectively
- The `<id>` is your game id, which can be set to any text. Players use it to join your game
- The `<mode>` specifies the game mode to play, which is either `default`, `one_bounce`, or `freeze_tag`
- The `<min>` and `<max>` specify the minimum and maximum players in your game. `<min>` must be at least 2, because
  there needs to be at least one killer and one survivor, and it can't be more than `<max>`
- The `<quick-joinable>` argument (`true` or `false`) specifies whether players can also join your game with
  `/murder game quick-join`, without needing an invite. This only works when quick-join is enabled (see
  [Quick-join Commands](#quick-join-commands))

```{figure} images/game.png
Example of creating a game using built-in commands
```

Once enough players have joined, start the game with `/murder game start`.

## About Game Modes
There are currently three game modes in Murder Run. There is your standard, default mode that is similar to Dead By
Daylight. There is also a One Bounce game mode, where there is only 1 survivor (and everyone else is a killer). Finally,
there is a Freeze Tag game mode, where survivors have a limited number of lives, and being killed results in you being
frozen until a fellow teammate revives you. Settings for each of these game modes can be tweaked in their own game
properties file (see [Game Properties](configuration.md#game-properties)).

## Parties Integration
You're able to use the [Parties](https://alessiodp.com/parties) plugin by AlessioDP Dev to create new games as well.
Create your own party by first using the `/party create` command. Then invite users by using the `/party invite`
command for them to join. In order to start a new game, use the `/murder game party <arena> <lobby>` command, where you
replace `<arena>` and `<lobby>` with the arena and lobby names respectively. Everyone in your party is added to the game.

## Quick-join Commands
Players are also able to join games without an invite by running the `/murder game quick-join` command. It puts them
into the next open quick-joinable game, or creates a new one from the arena-lobby pairs in the `quick-join.yml`
configuration file (see [Quick Join Configuration](configuration.md#quick-join-configuration)). The point of the
quick-join command is to easily set up new games without having to specify all the arguments above.

```{note}
Quick-join is **disabled by default**, and while it's disabled `/murder game quick-join` doesn't join any game, even
ones created as quick-joinable. Set `enabled: true` in `quick-join.yml` and add at least one arena-lobby pair to turn
it on. Make sure that you create your arena and lobby first, since Murder Run skips pairs it doesn't recognize.
```

---

# In-Game Commands
After setting up a new game for you and your friends, there are more commands that you may find useful.

## Getting the Resource Pack
If you didn't get the resource pack for some reason, use the `/murder resources` command to get the resource pack.

## Inviting Another Player
To invite another player, use the `/murder game invite <player>` command, replacing `<player>` with the name of the
player that you want to invite. The player will receive an invitation, and must either click on the accept message or
run the `/murder game join <id>` command, where `<id>` is the game id.

Another option is to use the built-in GUI to invite players. You can use the GUI to set players as survivors or killers
too. Reopen the GUI to refresh it. You can get the GUI by running the `/murder game gui` command once in-game.

```{figure} images/invitegui.png
Example of inviting or setting player roles by using the in-game GUI
```

## Kicking Another Player
To kick another player, use the `/murder game kick <player>` command, replacing `<player>` with the name of the player
you want to kick.

## Listing Game Members
To list all members in your current game, use the `/murder game list` command, which will list all survivors and killers
of the current game.

## Starting, Cancelling, or Leaving Games
To start the game, you must be the owner and run the `/murder game start` command once at least `<min>` players have
joined. To cancel a game, you must be the owner and run the `/murder game cancel` command, which will kick everyone out
of the current game. To leave a game as a normal player, run the `/murder game leave` command.

## Setting Somebody to be Killer
To set somebody to be the killer, use the `/murder game set murderer <player>` command, replacing `<player>` with the
name of the player you want to set as the killer. You can have multiple killers at one time.

## Setting Somebody to be Survivor
The survivor role is the default role, but if you accidentally set someone to killer, you can set them back to survivor
by using the `/murder game set innocent <player>` command, replacing `<player>` with the name of the player you want to
set as a survivor.

---

# Admin Commands
These are commands reserved for admins for testing or debugging purposes.

## Retrieving a Gadget
To retrieve a specific gadget, run the `/murder gadget retrieve <id>`, where `<id>` is the gadget id. You can also use
the `/murder gadget retrieve-all` command to just get all gadgets at once, but note this will overflow your inventory.

You may also use the `/murder gadget menu` command to see all gadgets in a GUI. Clicking on a gadget will automatically
give you that gadget.

```{figure} images/gadgets.png
Example of using the gadget GUI to get specific gadgets
```

## Retrieving an Ability
To retrieve a specific ability, run the `/murder ability retrieve <id>`, where `<id>` is the ability id. You can also
use the `/murder ability retrieve-all` command to just get all abilities at once, or the `/murder ability menu` command
to pick abilities from a GUI.

```{figure} images/abilities.png
Example of using the ability GUI to get specific abilities
```

## Other Admin Commands
- `/murder truck render` and `/murder truck destroy` render and remove an example truck made out of block displays
- `/murder dump` uploads your server information and latest log for support (see the warning on the
  [Commands and Permissions](commands.md#admin-and-debugging-commands) page)
