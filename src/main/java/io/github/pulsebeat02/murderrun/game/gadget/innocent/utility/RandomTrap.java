package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadgetManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class RandomTrap extends MurderGadget {

  public RandomTrap() {
    super(
        "random",
        Material.END_PORTAL,
        Locale.RANDOM_TRAP_NAME.build(),
        Locale.RANDOM_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final MurderGadgetManager manager = game.getGadgetManager();
    final Map<String, MurderGadget> map = manager.getGameGadgets();
    final Collection<MurderGadget> gadgets = map.values();
    if (gadgets.isEmpty()) {
      throw new AssertionError("No gadgets found!");
    }

    final List<MurderGadget> list = new ArrayList<>(gadgets);
    Collections.shuffle(list);

    final MurderGadget gadget = list.getFirst();
    final ItemStack stack = gadget.getGadget();
    final PlayerInventory inventory = player.getInventory();
    inventory.addItem(stack);
  }
}
