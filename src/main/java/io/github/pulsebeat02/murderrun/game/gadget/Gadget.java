package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Gadget {
  void onGadgetNearby(final Game game, final GamePlayer activator, final Item item);

  void onGadgetRightClick(final Game game, final PlayerInteractEvent event, final boolean remove);

  boolean onGadgetDrop(final Game game, GamePlayer player, final Item item, final boolean remove);

  ItemStack getGadget();

  String getName();

  int getPrice();

  @NonNull
  MerchantRecipe createRecipe();
}
