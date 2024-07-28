package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
import org.bukkit.Material;
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
        Locale.HACK_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final ItemStack stack = this.removeSwordItemStack(murderer);
    if (stack != null) {
      SchedulingUtils.scheduleTask(() -> this.giveSwordBack(murderer, stack), 7 * 20);
    }
  }

  private @Nullable ItemStack removeSwordItemStack(final GamePlayer player) {
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

  private void giveSwordBack(final GamePlayer player, final ItemStack stack) {
    final PlayerInventory inventory = player.getInventory();
    if (stack != null) {
      inventory.addItem(stack);
    }
  }
}
