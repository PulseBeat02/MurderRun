package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
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

public final class TrapVest extends SurvivorGadget {

  public TrapVest() {
    super(
        "trap_vest",
        Material.TNT,
        Locale.TRAP_VEST_TRAP_NAME.build(),
        Locale.TRAP_VEST_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.handleTraps(gamePlayer, location, world), false);
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
