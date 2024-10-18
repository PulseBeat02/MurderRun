package io.github.pulsebeat02.murderrun.game.gadget.packet;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class GadgetDropPacket {

  private final Game game;
  private final GamePlayer player;
  private final Item item;

  public GadgetDropPacket(final Game game, final GamePlayer player, final Item item) {
    this.game = game;
    this.player = player;
    this.item = item;
  }

  public static GadgetDropPacket create(final Game game, final PlayerDropItemEvent event) {
    final Player player = event.getPlayer();
    final Item item = event.getItemDrop();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    return new GadgetDropPacket(game, gamePlayer, item);
  }

  public Game getGame() {
    return this.game;
  }

  public GamePlayer getPlayer() {
    return this.player;
  }

  public Item getItem() {
    return this.item;
  }
}
