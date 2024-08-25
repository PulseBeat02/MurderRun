package io.github.pulsebeat02.murderrun.game.gadget.killer.trap;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Trap;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public abstract class KillerTrap extends Trap implements KillerApparatus {

  public KillerTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement,
      final int cost,
      final Color color) {
    super(name, material, itemName, itemLore, announcement, cost, color);
  }

  @Override
  public void onGadgetNearby(final Game game, final GamePlayer activator, final Item item) {
    this.onTrapActivate(game, activator, item);
    final Component announcement = this.getAnnouncement();
    final Component subtitle = empty();
    if (announcement != null) {
      final PlayerManager manager = game.getPlayerManager();
      manager.showTitleForAllMurderers(announcement, subtitle);
    }
  }
}
