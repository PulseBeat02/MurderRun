package io.github.pulsebeat02.murderrun.game.gadget.survivor.armor;

import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;

public final class SurvivorHelmet extends SurvivorGear {

  public SurvivorHelmet() {
    super("survivor_helmet", Material.DIAMOND_HELMET, Message.SURVIVOR_HELMET.build());
  }
}
