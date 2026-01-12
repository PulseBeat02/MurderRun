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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetLoadingMechanism;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.FallingBlockReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class SupplyDrop extends SurvivorGadget implements Listener {

  private final Game game;

  public SupplyDrop(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "supply_drop",
      properties.getSupplyDropCost(),
      ItemFactory.createGadget(
        "supply_drop",
        properties.getSupplyDropMaterial(),
        Message.SUPPLY_DROP_NAME.build(),
        Message.SUPPLY_DROP_LORE.build()
      )
    );
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final Location spawnLocation = location.add(0, 100, 0);
    final World world = requireNonNull(spawnLocation.getWorld());
    final BlockData data = Material.CHEST.createBlockData();
    final FallingBlock chest = world.spawnFallingBlock(spawnLocation, data);
    final PersistentDataContainer container = chest.getPersistentDataContainer();
    container.set(Keys.AIR_DROP, PersistentDataType.BOOLEAN, true);

    final GameScheduler scheduler = game.getScheduler();
    final FallingBlockReference reference = FallingBlockReference.of(chest);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticleTrail(chest), 0, 2, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(Sounds.SUPPLY_DROP);

    return false;
  }

  private void spawnParticleTrail(final FallingBlock chest) {
    final Location location = chest.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDropItemEvent(final EntityDropItemEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final FallingBlock fallingBlock)) {
      return;
    }

    final BlockData data = fallingBlock.getBlockData();
    final Material material = data.getMaterial();
    if (material != Material.CHEST) {
      return;
    }

    final PersistentDataContainer container = fallingBlock.getPersistentDataContainer();
    final Boolean value = container.get(Keys.AIR_DROP, PersistentDataType.BOOLEAN);
    if (value == null) {
      return;
    }

    final Location location = fallingBlock.getLocation();
    final Block block = location.getBlock();
    block.setType(Material.CHEST);
    fallingBlock.remove();

    final Chest chest = (Chest) block.getState();
    final ItemStack[] items = this.generateSupplyDropItems();
    final Inventory inventory = chest.getInventory();
    inventory.setStorageContents(items);

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onFallingLootCrate(final EntityChangeBlockEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final FallingBlock fallingBlock)) {
      return;
    }

    final Material material = event.getTo();
    if (material != Material.CHEST) {
      return;
    }

    final PersistentDataContainer container = fallingBlock.getPersistentDataContainer();
    final Boolean value = container.get(Keys.AIR_DROP, PersistentDataType.BOOLEAN);
    if (value == null) {
      return;
    }

    final Block block = event.getBlock();
    block.setType(Material.CHEST);

    final Chest chest = (Chest) block.getState();
    final ItemStack[] items = this.generateSupplyDropItems();
    final Inventory inventory = chest.getInventory();
    inventory.setStorageContents(items);
  }

  private ItemStack[] generateSupplyDropItems() {
    final int index = RandomUtils.generateInt(3);
    final GameProperties properties = this.game.getProperties();
    final String all = properties.getSupplyDropMasks();
    final String[] masks = all.split(",");
    final String mask = masks[index];
    final ItemStack[] items = new ItemStack[mask.length()];
    final GadgetManager manager = this.game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    for (int i = 0; i < mask.length(); i++) {
      final char c = mask.charAt(i);
      if (c == 'A') {
        final Gadget gadget = mechanism.getRandomInnocentGadget();
        final me.brandonli.murderrun.utils.item.Item.Builder stack = gadget.getStackBuilder();
        items[i] = stack.build();
      }
    }
    return items;
  }
}
