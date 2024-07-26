package io.github.pulsebeat02.murderrun.trap.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HackTrap extends SurvivorTrap {

  public HackTrap() {
    super(
        "Hack Trap",
        Material.EMERALD_BLOCK,
        Locale.HACK_TRAP_NAME.build(),
        Locale.HACK_TRAP_LORE.build(),
        Locale.HACK_TRAP_NAME.build());
  }

  @Override
  public void onDropEvent(final PlayerDropItemEvent event) {}

  @Override
  public void activate(final MurderGame game, final Murderer murderer) {
    super.activate(game, murderer);
    final ItemStack stack = this.removeSwordItemStack(murderer);
    if (stack != null) {
      this.scheduleTask(() -> this.giveSwordBack(murderer, stack), 20 * 10);
    }
  }

  private @Nullable ItemStack removeSwordItemStack(final Murderer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    ItemStack find = null;
    for (final ItemStack stack : slots) {
      if (!ItemStackUtils.isSword(stack)) {
        continue;
      }
      inventory.remove(stack);
      find = stack;
    }
    return find;
  }

  private void giveSwordBack(final Murderer player, final ItemStack stack) {
    final PlayerInventory inventory = player.getInventory();
    if (stack != null) {
      inventory.addItem(stack);
    }
  }
}
