package io.github.pulsebeat02.murderrun.game.gadget.innocent.armor;

import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;

public final class SurvivorHelmet extends SurvivorGear {

  public SurvivorHelmet() {
    super("survivor_helmet", Material.DIAMOND_HELMET, Locale.SURVIVOR_HELMET.build());
  }
}
