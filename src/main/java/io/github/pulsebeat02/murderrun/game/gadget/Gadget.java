package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public interface Gadget {
  void onGadgetNearby(final GadgetNearbyPacket packet);

  boolean onGadgetRightClick(final GadgetRightClickPacket packet);

  boolean onGadgetDrop(final GadgetDropPacket packet);

  ItemStack getGadget();

  String getName();

  int getPrice();

  MerchantRecipe createRecipe();
}
