package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.CitizensManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.MirrorTrait;
import net.citizensnpcs.trait.SleepTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlayerDeathTool {

  private final Game game;

  public PlayerDeathTool(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  public void initiateDeathSequence(final GamePlayer gamePlayer) {
    final DeathManager manager = gamePlayer.getDeathManager();
    gamePlayer.apply(player -> {
      manager.setCorpse(this.spawnDeadNPC(player));
      this.announcePlayerDeath(player);
      this.summonCarParts(player);
      this.preparePlayer(player);
    });
  }

  private NPC spawnDeadNPC(final Player player) {

    final CitizensManager manager = this.game.getNPCManager();
    final NPCRegistry registry = manager.getRegistry();
    final Location location = player.getLocation();
    final String name = player.getDisplayName();
    final NPC npc = registry.createNPC(EntityType.PLAYER, name);
    npc.setAlwaysUseNameHologram(false);

    final MirrorTrait mirror = npc.getOrAddTrait(MirrorTrait.class);
    mirror.isMirroring(player);
    mirror.setEnabled(true);

    final SleepTrait sleep = npc.getOrAddTrait(SleepTrait.class);
    sleep.setSleeping(location);

    final MetadataStore metadata = npc.data();
    metadata.set(Metadata.NAMEPLATE_VISIBLE, false);
    npc.spawn(location);

    final Entity entity = npc.getEntity();
    entity.setGravity(true);

    return npc;
  }

  private void preparePlayer(final Player player) {
    player.setGameMode(GameMode.SPECTATOR);
    final PlayerInventory inventory = player.getInventory();
    inventory.clear();
  }

  private void announcePlayerDeath(final Player dead) {
    final String name = dead.getDisplayName();
    final Component title = Message.PLAYER_DEATH.build(name);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
  }

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      if (!PDCUtils.isCarPart(slot)) {
        continue;
      }
      final Map map = this.game.getMap();
      final PartsManager manager = map.getCarPartManager();
      final CarPart stack = requireNonNull(manager.getCarPartItemStack(slot));
      final Location death = requireNonNull(player.getLocation());
      stack.setPickedUp(false);
      stack.setLocation(death);
      stack.spawn();
    }
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllDead(this::spawnParticleOnCorpse), 0, 20L);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {

    final DeathManager manager = gamePlayer.getDeathManager();
    final NPC stand = manager.getCorpse();
    if (stand == null) {
      return;
    }

    final Entity entity = stand.getEntity();
    if (entity.isDead()) {
      manager.setCorpse(null);
      return;
    }

    final Location location = stand.getStoredLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
  }
}
