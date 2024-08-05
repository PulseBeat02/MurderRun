package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.DeathTask;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class TrapVest extends MurderGadget {

  public TrapVest() {
    super(
        "trap_vest",
        Material.TNT,
        Locale.TRAP_VEST_TRAP_NAME.build(),
        Locale.TRAP_VEST_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final DeathTask task =
        new DeathTask(() -> this.handleTraps(gamePlayer, location, world), false);
    gamePlayer.addDeathTask(task);

    final Component message = Locale.TRAP_VEST_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }

  private void handleTraps(final GamePlayer player, final Location location, final World world) {

    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();

    for (final ItemStack slot : slots) {

      if (slot == null) {
        return;
      }

      final Item droppedItem = world.dropItem(location, slot);
      final Vector velocity = new Vector(
          (RandomUtils.generateDouble() - 0.5) * 2,
          RandomUtils.generateDouble() * 2,
          (RandomUtils.generateDouble() - 0.5) * 2);
      droppedItem.setVelocity(velocity);
    }
  }
}
