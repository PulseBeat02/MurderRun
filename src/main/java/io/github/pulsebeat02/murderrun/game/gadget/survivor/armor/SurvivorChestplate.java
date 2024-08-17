package io.github.pulsebeat02.murderrun.game.gadget.survivor.armor;

import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;

public final class SurvivorChestplate extends SurvivorGear {

  public SurvivorChestplate() {
    super("survivor_chestplate", Material.DIAMOND_CHESTPLATE, Message.SURVIVOR_CHESTPLATE.build());
  }
}
