package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.armor.SurvivorBoots;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.armor.SurvivorChestplate;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.armor.SurvivorHelmet;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.armor.SurvivorLeggings;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.tool.Excavator;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.tool.Flashlight;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.tool.KillerTracker;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.tool.Shield;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.BearTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.BlindTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.BurrowTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.CageTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.DistortTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.FartTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.FireworkTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.FreezeTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.GhostTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.GlowTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.HackTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.HauntTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.JebTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.JumpScareTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.LevitationTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.NeckSnapTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.PonyTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.ShockwaveTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SmokeTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SpasmTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SpawnTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.StarTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.BlastOff;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Bush;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Camera;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Chipped;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Cloak;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.CorpusWarp;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.CryoFreeze;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Deadringer;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Decoy;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Distorter;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Drone;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.FlashBang;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.FriendWarp;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Ghosting;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Horcrux;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.IceSkatin;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.IceSpirit;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.KillerRewind;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.LifeInsurance;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.MagnetMode;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.MedBot;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.MedKit;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.MindControl;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Parasite;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.PartSniffer;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.PortalTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.RandomTeleport;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.RandomTrap;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.ResurrectionStone;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Retaliation;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Rewind;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.SixthSense;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.SmokeGrenade;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.SpeedPendant;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.SupplyDrop;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Tracker;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.Translocator;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.utility.TrapVest;

public enum SurvivorGadgets {
  SURVIVOR_BOOTS(SurvivorBoots.class),
  SURVIVOR_CHESTPLATE(SurvivorChestplate.class),
  SURVIVOR_HELMET(SurvivorHelmet.class),
  SURVIVOR_LEGGINGS(SurvivorLeggings.class),
  EXCAVATOR(Excavator.class),
  SHIELD(Shield.class),
  BLAST_OFF(BlastOff.class),
  BUSH(Bush.class),
  CAMERA(Camera.class),
  CHIPPED(Chipped.class),
  CLOAK(Cloak.class),
  CORUPUS_WARP(CorpusWarp.class),
  CRYO_FREEZE(CryoFreeze.class),
  DEADRINGER(Deadringer.class),
  DECOY(Decoy.class),
  DISTORTER(Distorter.class),
  DRONE(Drone.class),
  FLASH_BANG(FlashBang.class),
  FLASHLIGHT(Flashlight.class),
  FRIEND_WARP(FriendWarp.class),
  GHOSTING(Ghosting.class),
  HORCRUX(Horcrux.class),
  ICE_SKATING(IceSkatin.class),
  ICE_SPIRIT(IceSpirit.class),
  KILLER_REWIND(KillerRewind.class),
  KILLER_TRACKER(KillerTracker.class),
  LIFE_INSURANCE(LifeInsurance.class),
  MAGNET_MODE(MagnetMode.class),
  MED_BOT(MedBot.class),
  MED_KIT(MedKit.class),
  MIND_CONTROL(MindControl.class),
  PARASITE(Parasite.class),
  PORTAL_TRAP(PortalTrap.class),
  RANDOM_TELEPORT(RandomTeleport.class),
  RANDOM_TRAP(RandomTrap.class),
  RESURRECTION_STONE(ResurrectionStone.class),
  RETALIATION(Retaliation.class),
  REWINDED(Rewind.class),
  SIXTH_SENSE(SixthSense.class),
  SMOKE_GRENADE(SmokeGrenade.class),
  SPEED_PENDANT(SpeedPendant.class),
  SUPPLY_DROP(SupplyDrop.class),
  TRACKER(Tracker.class),
  TRANSLOCATOR(Translocator.class),
  TRAP_SNIFFER(PartSniffer.class),
  TRAP_VEST(TrapVest.class),
  BEAR_TRAP(BearTrap.class),
  BLIND_TRAP(BlindTrap.class),
  BURROW_TRAP(BurrowTrap.class),
  CAGE_TRAP(CageTrap.class),
  DISTORT_TRAP(DistortTrap.class),
  FART_TRAP(FartTrap.class),
  FIREWORK_TRAP(FireworkTrap.class),
  FREEZE_TRAP(FreezeTrap.class),
  GHOST_TRAP(GhostTrap.class),
  GLOW_TRAP(GlowTrap.class),
  HACK_TRAP(HackTrap.class),
  HAUNT_TRAP(HauntTrap.class),
  JEB_TRAP(JebTrap.class),
  JUMP_SCARE_TRAP(JumpScareTrap.class),
  LEVITATION_TRAP(LevitationTrap.class),
  NECK_SNAP_TRAP(NeckSnapTrap.class),
  PONY_TRAP(PonyTrap.class),
  SHOCKWAVE_TRAP(ShockwaveTrap.class),
  SMOKE_TRAP(SmokeTrap.class),
  SPASM_TRAP(SpasmTrap.class),
  SPAWN_TRAP(SpawnTrap.class),
  STAR_TRAP(StarTrap.class);

  /*

  SURVIVOR TRAPS COMPLETELY DONE

  Fart Trap -- plays fart sound, gives killer nausea, slowness
  Jeb Trap -- places herd of rainbow sheep
  Jump Scare Trap -- jump scares if killer gets too close
  Hack Trap -- removes sword
  Bear Trap -- makes killer stuck and slowed for 10s
  Spasm Trap -- makes killer freak out alternating their head up and down
  Smoke Trap -- makes killer dizzy, blind and slowed
  Med Kit -- instantly heal health
  Diamond Armor -- gear
  Shield -- low durability take extra hits when held
  Speed Pendant - adds speed pendant
  Excavator -- destroy 10 blocks
  Cage Trap -- traps the killer in a cage
  Blind Trap -- blinds the killer who steps on it
  Neck Snap Trap -- snaps killer neck to always look up
  Spawn Trap -- sends killer back to spawn
  Freeze Trap -- freezes killer into place
  Pony Trap -- summons a fast horse
  Random Trap -- gets a random trap
  Ghost Trap -- makes all survivors invisible temporarily
  Haunt Trap -- covers killers screen with freaky effects
  Firework Trap -- shoots off fireworks when triggered
  Levitation Trap -- sends the killer into the sky temporarily
  Bush -- you become a bush for 10s
  Decoy -- place fake player with name
  /TP ME AWAY FROM HERE -- teleports you to a random spot on the map
  Blast Off -- sends the killer into a rocket into space
  Life Insurance -- if killer gets close, you teleport away (1 time use)
  Ice-Skatin -- spawns a boat that has ice underneath it
  Cryo-Freeze -- creates a huge ice dome around you
  Burrow Trap -- sends killer underground temporarily
  Star Trap -- buffs all survivors with speed, resistance, and regeneration when hit
  Deadringer -- fake player death, fake kill them and then they become invulnerable
  Resurrection Stone -- resurrects a dead player
  Friend Warp -- teleport to a random survivor
  Cloak -- all player usernames hidden for 30s
  Magnet Mode -- makes all trap activation range 3 times larger
  Corpus Warp -- teleports to a dead player
  Retaliation -- for each teammate death, you gain speed, resistance, and regeneration
  Flashbang -- stuns
  Smoke Grenade -- creates a huge smoke cloud
  Supply Drop -- get a various assortment of traps
  Trap Vest -- if a survivor uses it and dies, explodes all remaining traps on ground
  Killer Tracker -- tells you how close the killer is and how much danger you are in
  Flashlight -- blinds killer if come close, every 5 seconds
  Drone -- surveys map and fly around
  Distort Trap -- spawn guardians all over murderer until trap destroyed
  Translocator -- one use teleporter, (becomes lever to warp)
  Rewind -- rewinds player 5 seconds (must use circular buffer)
  Murderer Rewind -- rewinds murderer 5 seconds
  Horcrux -- respawn after death
  Shockwave Trap -- sets off massive blast flinging all players
  Chipped -- you can see all alive survivors on the map
  Glow Trap -- makes killer glow
  Sixth Sense -- if killer is near makes them glow
  Mind Control -- controls player mind
  Trap Sniffer -- senses detect car parts within 15 blocks
  Med Bot -- constant regeneration pool in area
  Portal Trap -- teleports trap to killer
  Camera -- if killer within range glow
  Ghosting -- become an annoying ghost after death
  Ice Spirit -- spawns an ice spirit that runs to the killer and freezes them
  Tracker -- if activated near killer you can always see them
  Distorter -- fills killer screen with annoying particles until destroyed
  Parasite -- spawns a parsetic vine that leeches player if too close (lower health, slow)

   */

  /*

  EXCLUDED TRAPS FROM SSUNDEE'S SERIES
  Traps here are excluded for one of the following reasons:
  - Trap isn't explained well
  - Trap pertains to a specific theme too much (for example, Fortnite)
  - Trap functions too similar to another trap
  - Trap is not balanced or not worth implementing
  - Trap has controversial topics (for example, clickbait, demonetization, etc).
  - Trap was superseded by another trap (for example, paint trap -> infrared vision)

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

  private final Class<?> clazz;

  SurvivorGadgets(final Class<?> clazz) {
    this.clazz = clazz;
  }

  public Class<?> getClazz() {
    return this.clazz;
  }
}
