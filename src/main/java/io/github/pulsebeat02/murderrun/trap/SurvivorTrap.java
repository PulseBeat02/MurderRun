package io.github.pulsebeat02.murderrun.trap;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract non-sealed class SurvivorTrap extends MurderTrap {

  /*

  SURVIVOR TRAPS ALREADY IMPLEMENTED:

  Glow Trap -- makes killer glow
  Hack Trap -- removes sword

  LIST OF ALL SURVIVOR TRAPS TO IMPLEMENT:

  Portal Trap -- teleports trap to killer
  Bear Trap -- makes killer stuck and slowed for 10s
  Spasm Trap -- makes killer freak out alternating their head up and down
  Rewind -- rewinds player 5 seconds (must use circular buffer)
  Murderer Rewind -- rewinds murderer 5 seconds
  Resurrection Stone -- resurrects a dead player
  Corpus Warp -- teleports to the nearest corpse
  Ghosting -- become a ghost after death
  Excavator -- destroy 10 blocks
  Distort Trap -- spawn guardians all over murderer until trap destroyed
  Horcrux -- respawn after death
  Med Bot -- constant heal pool in area
  Retaliation -- for each teammate death, you gain speed, resistance, and regeneration
  Supply Drop -- get a various assortment of traps
  Medkit -- instantly heal health
  Jump Scare Trap -- jump scares if killer gets too close
  Smoke Trap -- makes killer dizzy, blind and slowed
  Diamond Armor -- gear
  Levitation Trap -- sends the killer into the sky temporarily
  Cage Trap -- traps the killer in a cage
  Blind Trap -- blinds the killer who steps on it
  Haunt Trap -- covers killers screen with freaky effects
  Neck Snap Trap -- snaps killer neck to always look up
  Deadringer -- fake player death, fake kill them and then they become invulnerable
  Star Trap -- buffs all survivors with speed, resistance, and regneration when hit
  Spawn Trap -- sends killer back to spawn
  Freeze Trap -- freezes killer into place
  Burrow Trap -- sends killer underground temporarily
  Ghost Trap -- makes all survivors invisible temporarily
  Pony Trap -- summons a fast horse
  Firework Trap -- shoots off fireworks when triggered
  Fart Trap -- plays fart sound, gives killer nausea, slowness
  Trap Vest -- if survivor uses it and dies, explodes all remaining traps on ground
  Random Trap -- gets a random trap
  Magnet Mode -- makes all trap activation range 3 times larger
  Translocator -- one use teleporter, (becomes lever to warp)
  Tracker -- if used near killer you can always see them
  Taglock Needle -- voodoo puppet
  Decoy -- place fake player with nae
  Smoke Bomb -- if activated smoke bomb causes survivor invis
  Friend Warp -- teleport to a random survivor
  Flashbang -- stuns
  Camera -- if killer within range glow
  Cloak -- all player usernames hidden for 30s
  Shield -- low durability take extra hits when held
  /TP ME AWAY FROM HERE -- teleports you to a random spot on the map
  Sixth Sense -- if killer is near makes them glow
  Blast Off -- sends the killer into a rocket into space
  Drone -- roots you to the ground, but allows you to fly above and look
  Trap Sniffer -- senses detect car parts within 15 blocks
  Some Cake in Vegas -- Sends killer to gamble for a random debuff
  Chipped -- you can see all alive survivors on the map
  Life Insurance -- if killer gets close, you teleport away (1 time use)
  Cryo-Freeze -- creates a huge ice dome around you
  Ice-Skatin -- spawns a boat that has ice underneath it
  Ice Spirit -- spawns an ice spirit that runs to the killer and freezes them
  Mind Control -- controls player mind
  Jeb Trap -- places herd of rainbow sheep
  Bush -- you become a bush for 10s
  Fortnite Building -- build stairs, walls, floors
  Jack-Jack - Laser Eyes -- shooting survivor makes them faster, murderer burns them
  Violet - Force Field Rift -- hold killer place with a forcefield that increases in strength longer item is on ground
  Geiger Counter -- tells you how close the killer is and how much danger you are in
  Blind Charge Trap -- blinds killer if come close, unbreakable
  Launch Pad -- spawns launch pad that launches you into the air
  Impulse Grenade -- causes all players to be launched away
  Zarya - Gravitron Surge -- creates a rift in the sky and sucks player
  Lucio - Crank it UP! --
  Shockwave -- sets off massive blast flinging all players
  Parasite -- spawns a parsetic vine that leeches player if too close (lower health, slow)
  Porta-Fort -- spawns a portafort
  Distorter -- fills killer screen with annoying particles until destroyed
  Clickbait Trap -- if killer steps on it they are sent to a room full of clickbait
  Demonetized -- if activated then youtube police spawns on killer
  Jetpack -- you can fly temporarily

   */

  private final @NonNull Material material;
  private final @NonNull Component itemName;
  private final @NonNull Component itemLore;
  private final Component announcement;

  public SurvivorTrap(
      final @NonNull String name,
      final @NonNull Material material,
      final @NonNull Component itemName,
      final @NonNull Component itemLore,
      final Component announcement) {
    super(name);
    this.material = material;
    this.itemName = itemName;
    this.itemLore = itemLore;
    this.announcement = announcement;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  public Component getItemLore() {
    return this.itemLore;
  }

  public Component getItemName() {
    return this.itemName;
  }

  public Material getMaterial() {
    return this.material;
  }

  @Override
  public ItemStack constructItemStack(@UnderInitialization SurvivorTrap this) {
    final String name = AdventureUtils.serializeComponentToLegacy(this.itemName);
    final String rawLore = AdventureUtils.serializeComponentToLegacy(this.itemLore);
    final List<String> lore = List.of(rawLore);
    final ItemStack stack = new ItemStack(this.material);
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Unable to construct trap!");
    }
    meta.setDisplayName(name);
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public void activate(final MurderGame game, final Murderer murderer) {
    if (this.announcement == null) {
      return;
    }
    final MurderRun plugin = game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<GamePlayer> players = manager.getParticipants();
    for (final GamePlayer gamePlayer : players) {
      final Player player = gamePlayer.getPlayer();
      final Audience audience = audiences.player(player);
      audience.sendMessage(this.announcement);
    }
  }
}
