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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.FallingBlockReference;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
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
    super(
      "supply_drop",
      GameProperties.SUPPLY_DROP_COST,
      ItemFactory.createGadget("supply_drop", Material.CHEST, Message.SUPPLY_DROP_NAME.build(), Message.SUPPLY_DROP_LORE.build())
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
    final String all = GameProperties.SUPPLY_DROP_MASKS;
    final String[] masks = all.split(",");
    final String mask = masks[index];
    final ItemStack[] items = new ItemStack[mask.length()];
    final GadgetManager manager = this.game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    for (int i = 0; i < mask.length(); i++) {
      final char c = mask.charAt(i);
      if (c == 'A') {
        final Gadget gadget = mechanism.getRandomInnocentGadget();
        final io.github.pulsebeat02.murderrun.utils.item.Item.Builder stack = gadget.getStackBuilder();
        items[i] = stack.build();
      }
    }
    return items;
  }
}
