package io.github.pulsebeat02.murderrun.game.gadget.packet;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import org.bukkit.entity.Item;

public final class GadgetNearbyPacket {

  final Game game;
  final GamePlayer activator;
  final Item item;

  public GadgetNearbyPacket(final Game game, final GamePlayer activator, final Item item) {
    this.game = game;
    this.activator = activator;
    this.item = item;
  }

  public Game getGame() {
    return this.game;
  }

  public GamePlayer getActivator() {
    return this.activator;
  }

  public Item getItem() {
    return this.item;
  }
}
