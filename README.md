[![CodeFactor](https://www.codefactor.io/repository/github/pulsebeat02/murderrun/badge)](https://www.codefactor.io/repository/github/pulsebeat02/murderrun)
[![GitHub Actions](https://github.com/PulseBeat02/MurderRun/actions/workflows/tagged-release.yml/badge.svg)](https://github.com/PulseBeat02/MurderRun/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PulseBeat02_MurderRun&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PulseBeat02_MurderRun)

![Banner](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_header.webp)
[![GitHub](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_github.webp)](https://github.com/PulseBeat02/MurderRun)
[![Discord](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_discord.webp)](https://discord.gg/cUMB6kCsh6)
[![Kofi](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_kofi.webp)](https://ko-fi.com/pulsebeat_02)
![Description](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_description.webp)

Inspired by the popular game Dead by Daylight, Murder Run is an advanced gamemode revolving around killers
and survivors. In a desolate map, survivors must find all the vehicle parts and throw them back onto the
truck before the killers murder everyone. Both killers and survivors have access to a 100+ different gadgets, 
which can be used to gain advantages and disadvantages. This game is based off SSundee's "Murder Run" series
on YouTube. Here are some more awesome features about this plugin.

- Free and Open Source
- Over 100+ Killer/Survivor Gadgets
- Easy Lobby / Arena Creation and Customization
  - Either use GUI's or Commands to Create and Modify Arenas / Lobbies
- Resets Arenas Automatically using WorldEdit
- Customize Item Drop Locations
  - Supports Automatic Item Drop Locations
  - No Setup Required for Arenas
- Pre-made NPC Gadget Shop with Adjustable Prices
  - Prevent Certain Gadgets from Showing Up
- Game Creation with Quick Join System or Private Games
  - Either use GUI's or Commands to Create Games
- Integration with PlaceholderAPI, LibsDisguises, Citizens, and WorldEdit
  - Integrates with PlaceholderAPI for Custom Statistics
    - `%%fastest_win_killer%%`, `&&fastest_win_survivor&&`, `%%total_kills%%`, `%%total_deaths%%`,
    `%%total_wins%%`, `%%total_losses%%`, `%%total_games%%`, `%%win_loss_ratio%%`
  - Integrates with LibsDisguises for Custom Gadgets
  - PlaceholderAPI and LibsDisguises Not Required
- No Dependencies / Setup Involved, Just Drop the JAR into Plugins Folder
- Customisable Resource Pack
  - Customisable Item Textures, Sounds
  - Customisable Resource Pack Provider Method
  - No Server Hosting or Setup Required
    - Hosted on Server Automatically
    - Gives Option to Host via Other Options
  - Caches Resource Pack for Faster Loading Times
- Customisable Locale (Change / Reformat Messages)
  - Uses MiniMessage for Easy Formatting
- Support for Multiple Languages
  - Thank you **GTedd__** for Simplified and Traditional Chinese Locale Translations!
- Customisable Gadget Properties
  - Ability to Disable Gadgets
  - Ability to Modify Gadget Cooldowns, Sounds, and Potion Effects
- Customisable Game Properties
  - Ability to Set Game Timer
  - Ability to Set Extra Resource Packs
- Customisable Alive / Dead Chat
- Allows for Multiple Killers / Survivors
- Database Support via Hibernate (MySQL, SQLite, PostgreSQL, H2)
  - Customisable Database Properties
- And so much more...
<br></br>

![Screenshots](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_screenshots.webp)
![Survivor Gadgets](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/survivor.gif)
![Killer Gadgets](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/killer.gif)
![Gadgets](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/gadget.gif)
<br></br>

![Installation](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_installation.webp)
1) Download the plugin from the latest releases [here](https://github.com/PulseBeat02/MurderRun/releases/tag/latest).
2) Drop the plugin into your server's `plugins` folder and start the server. Make the plugin generate the default files
for you.
3) Configure the plugin properties to your liking inside the `murderrun` folder.
4) Run the `/murder gui` command to open a GUI. Create and modify any arenas and/or lobbies to your liking. In your lobby, 
you can run `/murder npc spawn survivor` and `/murder npc spawn killer` to spawn both the survivor and the killer gadget 
shop, which can be used to buy gadgets.
5) Run the `/murder gui` command and configure and create a new game.
6) Click on the bold aqua message in chat to invite other players. Close the menu and re-open the menu to update
the player list. You can also change the roles of other players from survivor to killer and vice versa
by clicking on their player head (assuming that they have joined the game already).
<br></br>

![FAQ](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_faq.webp)
- **Configuration**: There are multiple ways to configure the plugin. You can customize the locale by editing the messages in the
`murderrun/locale/murderrun_XX_XX.properties` file, and change formatting using MiniMessage. You are also able to edit
game-specific properties in the `murderrun/settings/game.properties` file, such as gadget sounds, durations, effects,
pauses, and much more. Modify the resource pack sounds and textures by changing their respective files in the
`murderrun/sounds` and `murderrun/textures` folders. Resource packs are built at runtime and served using MC Packs
by default, but you are able to self-host an HTTP server on your server if wanted. Check the `config.yml` file to
configure JSON or database options, HTTP server options, languages, and so much more.
<br></br>

- **Commands**: Use the `/murder help` command to generate a list of commands in-game, and see what each command does. You are able
to retrieve specific gadget items, handle lobbies/arenas/games, create default NPC shops, and so much more via
this plugin.
<br></br>

- **Permissions**: Permissions are based on the command. For example, the `/murder command gadget retrieve-all` command would have the
`murderrun.command.gadget.retrieve-all` permission. Follow the same format for the rest of the plugin commands. It's
recommended that you use LuckPerms to handle your permissions.