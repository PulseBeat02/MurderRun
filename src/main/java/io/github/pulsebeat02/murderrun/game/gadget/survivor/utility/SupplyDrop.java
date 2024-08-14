package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
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

public final class SupplyDrop extends SurvivorGadget {

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
        Locale.SUPPLY_DROP_TRAP_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final Location spawnLocation = location.add(0, 50, 0);
    final World world = requireNonNull(spawnLocation.getWorld());
    final BlockData data = Material.CHEST.createBlockData();
    final FallingBlock chest = world.spawnFallingBlock(spawnLocation, data);
    chest.setDropItem(false);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticleTrail(game, chest), 0, 2);
  }

  private void spawnParticleTrail(final Game game, final FallingBlock chest) {

    final Location location = chest.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.CLOUD, location, 5, 0.2, 0.2, 0.2);

    final boolean grounded = chest.isOnGround() || chest.isDead();
    if (grounded) {
      final Location chestLocation = chest.getLocation();
      this.handleChestInventory(game, chestLocation);
    }
  }

  private void handleChestInventory(final Game game, final Location location) {

    final World world = requireNonNull(location.getWorld());
    final Block block = world.getBlockAt(location);
    if (!(block instanceof final Chest chest)) {
      return;
    }

    final ItemStack[] items = this.generateSupplyDropItems(game);
    final Inventory inventory = chest.getInventory();
    inventory.setStorageContents(items);
  }

  private ItemStack[] generateSupplyDropItems(final Game game) {
    final int index = RandomUtils.generateInt(3);
    final String mask = AIR_DROP_MASKS[index];
    final ItemStack[] items = new ItemStack[mask.length()];
    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    for (int i = 0; i < mask.length(); i++) {
      final char c = mask.charAt(i);
      if (c == 'A') {
        final Gadget gadget = mechanism.getRandomGadget();
        final ItemStack stack = gadget.getGadget();
        items[i] = stack;
      }
    }
    return items;
  }
}
