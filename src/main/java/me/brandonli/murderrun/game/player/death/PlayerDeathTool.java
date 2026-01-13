/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.extension.citizens.CitizensManager;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.PDCUtils;
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
      this.summonCarParts(player);
      this.preparePlayer(player);
    });
  }

  public NPC spawnDeadNPC(final Player player) {
    final GameExtensionManager extensionManager = this.game.getExtensionManager();
    final CitizensManager manager = extensionManager.getNPCManager();
    final NPCRegistry registry = manager.getRegistry();
    final Location location = player.getLocation();
    final Component component = player.displayName();
    final String name = ComponentUtils.serializeComponentToLegacyString(component);
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

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    @SuppressWarnings("all") // checker
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      if (!PDCUtils.isCarPart(slot)) {
        continue;
      }

      final GameMap map = this.game.getMap();
      final PartsManager manager = map.getCarPartManager();
      final CarPart stack = manager.getCarPartItemStack(slot);
      if (stack == null) {
        continue;
      }

      final Location death = requireNonNull(player.getLocation());
      stack.setPickedUp(false);
      stack.setLocation(death);
      stack.spawn();
    }
  }

  public void spawnParticles() {
    final NullReference reference = NullReference.of();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> manager.applyToDeceased(this::spawnParticleOnCorpse), 0, 20L, reference);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {
    final DeathManager manager = gamePlayer.getDeathManager();
    final NPC stand = manager.getCorpse();
    if (stand == null) {
      return;
    }

    final Entity entity = stand.getEntity();
    if (entity == null || entity.isDead()) {
      manager.setCorpse(null);
      return;
    }

    final Location location = stand.getStoredLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
  }
}
