package io.github.pulsebeat02.murderrun.game.gadget.survivor.armor;

import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;

public final class SurvivorBoots extends SurvivorGear {

  public SurvivorBoots() {
    super("survivor_boots", Material.DIAMOND_BOOTS, Message.SURVIVOR_BOOTS.build());
  }
}
