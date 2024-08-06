package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadgetManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class SupplyDrop extends MurderGadget {

  private static final String[] AIR_DROP_MASKS = {
    """
      AXAXAXAXA
      XAXAXAXAX
      AXAXAXAXA
      """,
    """
      XXXXAXXXX
      XAXAAAXAX
      XXXXAXXXX
      """,
    """
      AAXXAXXAA
      AXAXAXAXA
      AAXXAXXAA
      """
  };

  public SupplyDrop() {
    super(
        "supply_drop",
        Material.CHEST,
        Locale.SUPPLY_DROP_TRAP_NAME.build(),
        Locale.SUPPLY_DROP_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Location spawnLocation = location.add(0, 50, 0);
    final World world = spawnLocation.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final BlockData data = Material.CHEST.createBlockData();
    final FallingBlock chest = world.spawnFallingBlock(spawnLocation, data);
    chest.setDropItem(false);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticleTrail(game, chest), 0, 2);
  }

  private void spawnParticleTrail(final MurderGame game, final FallingBlock chest) {

    final Location location = chest.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
    world.spawnParticle(Particle.CLOUD, location, 5, 0.2, 0.2, 0.2);

    final boolean grounded = chest.isOnGround() || chest.isDead();
    if (grounded) {
      final Location chestLocation = chest.getLocation();
      this.handleChestInventory(game, chestLocation);
    }
  }

  private void handleChestInventory(final MurderGame game, final Location location) {

    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Block block = world.getBlockAt(location);
    if (!(block instanceof final Chest chest)) {
      return;
    }

    final ItemStack[] items = this.generateSupplyDropItems(game);
    final Inventory inventory = chest.getInventory();
    inventory.setStorageContents(items);
  }

  private ItemStack[] generateSupplyDropItems(final MurderGame game) {
    final int index = RandomUtils.generateInt(3);
    final String mask = AIR_DROP_MASKS[index];
    final ItemStack[] items = new ItemStack[mask.length()];
    for (int i = 0; i < mask.length(); i++) {
      final char c = mask.charAt(i);
      if (c == 'A') {
        final MurderGadget gadget = this.getRandomGadget(game);
        final ItemStack stack = gadget.getGadget();
        items[i] = stack;
      }
    }
    return items;
  }

  private MurderGadget getRandomGadget(final MurderGame game) {

    final MurderGadgetManager manager = game.getGadgetManager();
    final Map<String, MurderGadget> map = manager.getGameGadgets();
    final Collection<MurderGadget> gadgets = map.values();
    if (gadgets.isEmpty()) {
      throw new AssertionError("No gadgets found!");
    }

    final List<MurderGadget> list = new ArrayList<>(gadgets);
    Collections.shuffle(list);

    return list.getFirst();
  }
}
