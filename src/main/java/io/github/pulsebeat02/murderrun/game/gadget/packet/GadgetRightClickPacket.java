package io.github.pulsebeat02.murderrun.game.gadget.packet;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class GadgetRightClickPacket {

  private final Game game;
  private final GamePlayer player;
  private final ItemStack itemStack;

  public GadgetRightClickPacket(final Game game, final GamePlayer player, final ItemStack itemStack) {
    this.game = game;
    this.player = player;
    this.itemStack = itemStack;
  }

  public static GadgetRightClickPacket create(final Game game, final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final ItemStack item = requireNonNull(event.getItem());
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    return new GadgetRightClickPacket(game, gamePlayer, item);
  }

  public Game getGame() {
    return this.game;
  }

  public GamePlayer getPlayer() {
    return this.player;
  }

  public ItemStack getItemStack() {
    return this.itemStack;
  }
}
