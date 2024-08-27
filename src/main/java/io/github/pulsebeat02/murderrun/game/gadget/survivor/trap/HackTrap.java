package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HackTrap extends SurvivorTrap {

  private static final int HACK_TRAP_DURATION = 7 * 20;
  private static final String HACK_TRAP_SOUND = "entity.witch.celebrate";

  public HackTrap() {
    super(
        "hack",
        Material.EMERALD_BLOCK,
        Message.HACK_NAME.build(),
        Message.HACK_LORE.build(),
        Message.HACK_ACTIVATE.build(),
        48,
        Color.GREEN);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final PlayerInventory inventory = murderer.getInventory();
    final ItemStack stack = this.getSword(inventory);
    if (stack == null) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> inventory.addItem(stack), HACK_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(HACK_TRAP_SOUND);
  }

  private @Nullable ItemStack getSword(final PlayerInventory inventory) {
    final ItemStack[] slots = inventory.getContents();
    ItemStack find = null;
    for (final ItemStack stack : slots) {
      if (PDCUtils.isSword(stack)) {
        find = stack;
        inventory.remove(find);
        break;
      }
    }
    return find;
  }
}
