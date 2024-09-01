package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class RandomTrap extends SurvivorGadget {

  public RandomTrap() {
    super(
        "random",
        Material.END_PORTAL_FRAME,
        Message.RANDOM_NAME.build(),
        Message.RANDOM_LORE.build(),
        GameProperties.RANDOM_TRAP_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Gadget gadget = mechanism.getRandomInnocentGadget();
    final ItemStack stack = gadget.getGadget();

    final PlayerInventory inventory = player.getInventory();
    inventory.addItem(stack);

    return false;
  }
}
