package io.github.pulsebeat02.murderrun.game.gadget.survivor.armor;

import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;

public final class SurvivorLeggings extends SurvivorGear {

  public SurvivorLeggings() {
    super("survivor_leggings", Material.DIAMOND_LEGGINGS, Message.SURVIVOR_LEGGINGS.build());
  }
}
