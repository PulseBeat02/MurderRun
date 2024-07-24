package io.github.pulsebeat02.murderrun.trap;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract sealed class MurderTrap permits SurvivorTrap, KillerTrap {

  private static final NamespacedKey KEY = new NamespacedKey("murder_run", "trap");

  private final ItemStack stack;
  private final String name;

  public MurderTrap(final String name) {
    this.name = name;
    this.stack = this.constructItemStack();
  }

  public static NamespacedKey getPDCKey() {
    return KEY;
  }

  public abstract ItemStack constructItemStack();

  public abstract void onDropEvent(final PlayerDropItemEvent event);

  public abstract void activate(final MurderGame game);
}
