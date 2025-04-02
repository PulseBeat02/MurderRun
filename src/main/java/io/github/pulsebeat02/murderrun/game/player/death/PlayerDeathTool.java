/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.extension.GameExtensionManager;
import io.github.pulsebeat02.murderrun.game.extension.citizens.CitizensManager;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.MirrorTrait;
import net.citizensnpcs.trait.SleepTrait;
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

  private NPC spawnDeadNPC(final Player player) {
    final GameExtensionManager extensionManager = this.game.getExtensionManager();
    final CitizensManager manager = extensionManager.getNPCManager();
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

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
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
