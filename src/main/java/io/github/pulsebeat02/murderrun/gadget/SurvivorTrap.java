package io.github.pulsebeat02.murderrun.gadget;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract non-sealed class SurvivorTrap extends MurderTrap {

  /*

  SURVIVOR TRAPS ALREADY IMPLEMENTED:

  Glow Trap -- makes killer glow
  Hack Trap -- removes sword
  Bear Trap -- makes killer stuck and slowed for 10s
  Corpus Warp -- teleports to a dead player
  Distort Trap -- spawn guardians all over murderer until trap destroyed
  Spasm Trap -- makes killer freak out alternating their head up and down
  Jump Scare Trap -- jump scares if killer gets too close
  Smoke Trap -- makes killer dizzy, blind and slowed
  Med Kit -- instantly heal health
  Diamond Armor -- gear
  Excavator -- destroy 10 blocks
  Levitation Trap -- sends the killer into the sky temporarily
  Cage Trap -- traps the killer in a cage
  Blind Trap -- blinds the killer who steps on it
  Haunt Trap -- covers killers screen with freaky effects
  Neck Snap Trap -- snaps killer neck to always look up
  Star Trap -- buffs all survivors with speed, resistance, and regeneration when hit
  Spawn Trap -- sends killer back to spawn
  Freeze Trap -- freezes killer into place
  Burrow Trap -- sends killer underground temporarily
  Ghost Trap -- makes all survivors invisible temporarily
  Pony Trap -- summons a fast horse
  Firework Trap -- shoots off fireworks when triggered
  Fart Trap -- plays fart sound, gives killer nausea, slowness
  Decoy -- place fake player with name
  Friend Warp -- teleport to a random survivor
  Cloak -- all player usernames hidden for 30s
  Shield -- low durability take extra hits when held
  /TP ME AWAY FROM HERE -- teleports you to a random spot on the map
  Speed Pendant - adds speed pendant
  Camera -- if killer within  range glow
  Sixth Sense -- if killer is near makes them glow
  Trap Sniffer -- senses detect car parts within 15 blocks
  Chipped -- you can see all alive survivors on the map
  Rewind -- rewinds player 5 seconds (must use circular buffer)
  Murderer Rewind -- rewinds murderer 5 seconds
  Retaliation -- for each teammate death, you gain speed, resistance, and regeneration
  Med Bot -- constant regeneration pool in area
  Life Insurance -- if killer gets close, you teleport away (1 time use)
  Ice-Skatin -- spawns a boat that has ice underneath it
  Ice Spirit -- spawns an ice spirit that runs to the killer and freezes them
  Bush -- you become a bush for 10s
  Jeb Trap -- places herd of rainbow sheep
  Shockwave Trap -- sets off massive blast flinging all players
  Tracker -- if activated near killer you can always see them
  Deadringer -- fake player death, fake kill them and then they become invulnerable
  Distorter -- fills killer screen with annoying particles until destroyed

  LIST OF ALL SURVIVOR TRAPS TO IMPLEMENT:

  Portal Trap -- teleports trap to killer
  Ghosting -- become a ghost after death
  Horcrux -- respawn after death
  Resurrection Stone -- resurrects a dead player
  Supply Drop -- get a various assortment of traps
  Trap Vest -- if survivor uses it and dies, explodes all remaining traps on ground
  Random Trap -- gets a random trap
  Magnet Mode -- makes all trap activation range 3 times larger
  Translocator -- one use teleporter, (becomes lever to warp)
  Smoke Bomb -- if activated smoke bomb causes survivor invis
  Flashbang -- stuns
  Blast Off -- sends the killer into a rocket into space
  Drone -- roots you to the ground, but allows you to fly above and look
  Cryo-Freeze -- creates a huge ice dome around you
  Mind Control -- controls player mind
  Killer Tracker -- tells you how close the killer is and how much danger you are in
  Flashlight -- blinds killer if come close, every 5 seconds
  Parasite -- spawns a parsetic vine that leeches player if too close (lower health, slow)

   */

  /*

  EXCLUDED TRAPS FROM SSUNDEE'S SERIES
  Traps here are excluded for one of the following reasons:
  - Trap isn't explained well
  - Trap pertains to a specific theme too much (for example, Fortnite)
  - Trap functions too similar to another trap
  - Trap isn't balanced
  - Trap has controversial topics (for example, clickbait, demonetization, etc).

  Jack-Jack - Laser Eyes -- shooting survivor makes them faster, murderer burns them
  Violet - Force Field Rift -- hold killer place with a force field that increases in strength longer item is on ground
  Zarya - Gravitron Surge -- creates a rift in the sky and sucks player
  Lucio - Crank it UP! -- ??
  Fortnite Building -- build stairs, walls, floors
  Taglock Needle -- voodoo puppet
  Jetpack -- you can fly temporarily
  Launch Pad -- spawns launch pad that launches you into the air
  Impulse Grenade -- causes all players to be launched away
  Porta-Fort -- spawns a portafort
  Demonetized -- if activated then youtube police spawns on killer
  Clickbait Trap -- if killer steps on it they are sent to a room full of clickbait
  Some Cake in Vegas -- Sends killer to gamble for a random debuff

   */

  public SurvivorTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement) {
    super(name, material, itemName, itemLore, announcement);
  }

  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event, false);
  }
}
