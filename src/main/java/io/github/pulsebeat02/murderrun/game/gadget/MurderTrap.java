package io.github.pulsebeat02.murderrun.game.gadget;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public abstract sealed class MurderTrap extends MurderGadget permits SurvivorTrap, KillerTrap {

  private final Component announcement;

  public MurderTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement) {
    super(name, material, itemName, itemLore);
    this.announcement = announcement;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  @Override
  public void onGadgetNearby(final MurderGame game, final GamePlayer activator) {
    this.onTrapActivate(game, activator);
    if (this.announcement != null) {
      final MurderPlayerManager manager = game.getPlayerManager();
      manager.applyToAllParticipants(player -> player.showTitle(this.announcement, empty()));
    }
  }

  public abstract void onTrapActivate(final MurderGame game, final GamePlayer activee);
}
