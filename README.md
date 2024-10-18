# Murder Run

[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/4bRcTwC4GazhXHisYNoq6N/DEXVUEzbq9mmu7ERauj15L/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/circleci/4bRcTwC4GazhXHisYNoq6N/DEXVUEzbq9mmu7ERauj15L/tree/main)
[![CodeFactor](https://www.codefactor.io/repository/github/pulsebeat02/murderrun/badge)](https://www.codefactor.io/repository/github/pulsebeat02/murderrun)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PulseBeat02_MurderRun&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PulseBeat02_MurderRun)

[![BisectHosting](https://www.bisecthosting.com/partners/custom-banners/db8711d9-0b3a-4706-b18d-beb9cef16963.webp)](https://bisecthosting.com/pulse)

Want to show your support for me and the development of MurderRun? Check out BisectHosting and use the `pulse` 
for a 25% discount on your first month of a gaming server as a new customer. With their 24/7 support and quick 
response times, you can count on excellent assistance for all your gaming requirements.

## Inspiration
Based on the popular game Dead by Daylight, Murder Run is an advanced mini-game revolving around killers
and survivors. In a desolate map, survivors must find all the vehicle parts and throw them back onto the
truck before the killers murder everyone. Both killers and survivors have access to a 100+ different gadgets,
which can be used in combination to try and win. This game was also inspired by SSundee's "Murder Run" series
on YouTube.

## Configuration
Every-single message of this plugin is configurable inside the `/locale/murderrun_en.properties` file. This
includes user-interface message components as well! You are also able to tweak any specific game settings by
editing properties in `/settings/game.properties`, such as the sounds made by each gadget, their durations,
and so much more. You can also change the sounds and textures of the resource-pack provided by changing their
respective files in the `/sounds` and `/textures` folders. Resource-packs are built at runtime and served
using MC Pack Hosting by default. Check the `config.yml` file for more specific details.

## Installation
1) Build the plugin using `/gradlew build` or find the respective build by clicking the CircleCI badge
on the top of this README file. Using the JAR, place it into the plugins folder as well.
2) Run the plugin once to generate all assets and configuration files. Configure the plugin to your liking.
3) Run the `/murder gui` command to open a GUI that is able to create arenas and lobbies for you.
4) In your lobby, you can run `/murder npc spawn survivor` and `/murder npc spawn killer` to spawn both the
survivor and the killer gadget shop, which can be used to buy gadgets.
5) Run the `/murder gui` command and create a new game. Choose your arena and lobby and start the game.
6) Click on the message in chat to invite other players. Close the menu and re-open the menu to update
the player list. You can also change the roles of other players from survivor to killer and vice versa
by clicking on their player head (assuming that they have joined the game already).

## Building
1) Install [Jetbrains Runtime](https://github.com/JetBrains/JetBrainsRuntime) (Java 21)
   - Used for hot swapping purposes and faster development. Set the project JDK to be this for Gradle to work.
2) Run `gradlew build`
