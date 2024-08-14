package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Gadget {

  ItemStack constructItemStack(
      @UnderInitialization Gadget this,
      final String pdcName,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer);

  void onGadgetNearby(final Game game, final GamePlayer activator);

  void onGadgetRightClick(final Game game, final PlayerInteractEvent event, final boolean remove);

  void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove);

  ItemStack getGadget();

  String getName();

  int getPrice();
}
