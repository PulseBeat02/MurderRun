package io.github.pulsebeat02.murderrun.game.gadget.killer.trap;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Trap;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public abstract class KillerTrap extends Trap implements KillerApparatus {

  public KillerTrap(
    final String name,
    final Material material,
    final Component itemName,
    final Component itemLore,
    final Component announcement,
    final int cost,
    final Color color
  ) {
    super(name, material, itemName, itemLore, announcement, cost, color);
  }

  @Override
  public void onGadgetNearby(final GadgetNearbyPacket packet) {
    final Component announcement = this.getAnnouncement();
    final Component subtitle = empty();
    if (announcement != null) {
      final Game game = packet.getGame();
      final PlayerManager manager = game.getPlayerManager();
      manager.showTitleForAllMurderers(announcement, subtitle);
    }
  }
}
